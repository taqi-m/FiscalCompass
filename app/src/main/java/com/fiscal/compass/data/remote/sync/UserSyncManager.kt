package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.UserDao
import com.fiscal.compass.data.local.model.UserEntity
import com.fiscal.compass.domain.sync.SyncTimestampManager
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.mappers.toUserEntity
import com.fiscal.compass.domain.model.sync.SyncType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages synchronization of users between Firebase Firestore and local database.
 * 
 * This class focuses exclusively on Firebase remote operations:
 * - Downloading users from Firestore and persisting to local DB
 * - Uploading users from local DB to Firestore
 * - Updating user data in Firestore
 * 
 * Note: For local data access operations, use UserRepository.
 * This sync manager only handles remote sync operations.
 * 
 * Authorization:
 * - All authorization rules are enforced by Firebase Security Rules
 * - This manager propagates Firebase exceptions to the caller
 * 
 * Sync Strategy:
 * - Uses SyncTimestampManager to track last sync time
 * - Only downloads users modified after last sync timestamp
 * - Updates timestamp after successful sync
 */
@Singleton
class UserSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val timestampManager: SyncTimestampManager
) {
    
    companion object {
        private const val TAG = "UserSyncManager"
        private const val USERS_COLLECTION = "users"
    }
    
    // ========================================
    // PUBLIC SYNC OPERATIONS (Firebase ↔ Local)
    // ========================================
    
    /**
     * Downloads all users from Firebase Firestore to local database.
     * Uses timestamp-based sync to only fetch users modified after last sync.
     * Firebase Security Rules determine if the current user has permission to read all users.
     * 
     * @param forceRefresh If true, fetches all users from Firebase regardless of last sync time
     * @return List of synced UserEntity objects
     * @throws Exception if Firebase operation fails (including permission denied)
     */
    suspend fun downloadAllUsers(forceRefresh: Boolean = false): List<UserEntity> {
        Log.d(TAG, "Starting download of all users (forceRefresh: $forceRefresh)")
        
        // Get last sync timestamp
        val lastSyncTimestamp = if (forceRefresh) {
            0L // Fetch all users
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.USERS, "global")
        }
        
        Log.d(TAG, "Last user sync timestamp: $lastSyncTimestamp")
        
        // If no force refresh and we have local data, check if remote has updates
        if (!forceRefresh && lastSyncTimestamp > 0L) {
            val localUsers = getLocalUsersInternal()
            if (localUsers.isNotEmpty()) {
                Log.d(TAG, "Local users present (${localUsers.size} users). Checking for remote updates...")
            }
        }
        
        // Fetch users from Firebase that were updated after last sync
        val usersQuery = if (lastSyncTimestamp > 0L) {
            firestore.collection(USERS_COLLECTION)
                .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTimestamp)))
        } else {
            firestore.collection(USERS_COLLECTION)
        }
        
        val usersSnapshot = usersQuery.get().await()
        
        if (usersSnapshot.isEmpty) {
            Log.d(TAG, "No updated users found in Firebase since last sync")
            return getLocalUsersInternal()
        }
        
        Log.d(TAG, "Found ${usersSnapshot.documents.size} updated users in Firebase")
        
        // Convert documents to UserEntity
        val users = usersSnapshot.documents.mapNotNull { doc ->
            try {
                doc.data?.toUserEntity(doc.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing user document ${doc.id}", e)
                null
            }
        }
        
        if (users.isEmpty()) {
            Log.w(TAG, "No valid users could be parsed from Firebase")
            return getLocalUsersInternal()
        }
        
        Log.d(TAG, "Successfully parsed ${users.size} users")
        
        // Persist all users to local database
        persistUsersInternal(users)
        
        // Update last sync timestamp
        timestampManager.updateLastSyncTimestamp(SyncType.USERS, System.currentTimeMillis())
        
        Log.d(TAG, "Successfully downloaded and persisted ${users.size} users to local database")
        
        // Return all local users (including previously synced ones)
        return getLocalUsersInternal()
    }
    
    /**
     * Downloads a specific user from Firebase Firestore to local database.
     * Updates the user's local data and sync timestamp.
     * 
     * @param userId The ID of the user to download
     * @return UserEntity object
     * @throws Exception if user not found or Firebase operation fails
     */
    suspend fun downloadUser(userId: String): UserEntity {
        Log.d(TAG, "Downloading user: $userId")
        
        val userDoc = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
        
        if (!userDoc.exists()) {
            val error = "User $userId not found in Firebase"
            Log.w(TAG, error)
            throw NoSuchElementException(error)
        }
        
        val userData = userDoc.data
        if (userData == null) {
            val error = "User $userId has no data in Firebase"
            Log.w(TAG, error)
            throw IllegalStateException(error)
        }
        
        val user = userData.toUserEntity(userId)
            ?: throw IllegalStateException("Failed to parse user $userId")
        
        // Persist user to local database
        persistUsersInternal(listOf(user))
        
        // Update sync timestamp for this specific user download
        timestampManager.updateLastSyncTimestamp(SyncType.USERS, System.currentTimeMillis())
        
        Log.d(TAG, "Successfully downloaded and persisted user: $userId")
        
        return user
    }
    
    // ========================================
    // UPLOAD OPERATIONS (Local → Firebase)
    // ========================================
    
    /**
     * Uploads a user to Firebase Firestore.
     * 
     * @param user The UserEntity to upload
     * @throws Exception if Firebase operation fails (including permission denied)
     */
    suspend fun uploadUser(user: UserEntity) {
        Log.d(TAG, "Uploading user: ${user.userId}")
        
        val userMap = user.toFirestoreMap()
        
        firestore.collection(USERS_COLLECTION)
            .document(user.userId)
            .set(userMap)
            .await()
        
        Log.d(TAG, "Successfully uploaded user: ${user.userId}")
    }
    
    /**
     * Updates specific fields of a user in Firebase Firestore.
     * After updating, downloads the updated user to sync with local database.
     * 
     * @param userId The ID of the user to update
     * @param updates Map of field names to their new values
     * @return Updated UserEntity
     * @throws Exception if Firebase operation fails (including permission denied)
     */
    suspend fun updateUserFields(userId: String, updates: Map<String, Any>): UserEntity {
        Log.d(TAG, "Updating user fields for: $userId")
        
        // Add updatedAt timestamp
        val updatesWithTimestamp = updates.toMutableMap().apply {
            put("updatedAt", Timestamp(Date()))
        }
        
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .update(updatesWithTimestamp)
            .await()
        
        Log.d(TAG, "Successfully updated user fields for: $userId")
        
        // Download the updated user to sync with local database
        return downloadUser(userId)
    }
    
    // ========================================
    // INITIALIZATION OPERATIONS
    // ========================================
    
    /**
     * Initializes user data by downloading all users from Firebase.
     * Firebase Security Rules determine if the current user has permission to read all users.
     * Typically called during app initialization.
     * 
     * @param forceRefresh If true, fetches all users from Firebase regardless of local cache
     * @return List of synced UserEntity objects
     * @throws Exception if Firebase operation fails (including permission denied)
     */
    suspend fun initializeUsers(forceRefresh: Boolean = false): List<UserEntity> {
        Log.d(TAG, "Initializing user sync...")
        return downloadAllUsers(forceRefresh)
    }
    
    // ========================================
    // PRIVATE HELPER METHODS
    // ========================================
    
    /**
     * Internal method to get all users from local database.
     * Used for checking cache before Firebase download.
     */
    private suspend fun getLocalUsersInternal(): List<UserEntity> {
        return userDao.getAllUsersSync()
    }
    
    /**
     * Internal method to persist users to local database.
     * Used by sync operations after downloading from Firebase.
     */
    private suspend fun persistUsersInternal(users: List<UserEntity>) {
        userDao.insertUsers(users)
    }
}
