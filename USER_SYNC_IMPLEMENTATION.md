# User Sync Implementation for Admin Mode

## Overview
This document describes the implementation of user synchronization from Firebase to local database, enabling admin mode to sync income and expense data for all users on initial launch.

## Problem Statement
Previously, when the app launched in admin mode, there were no users in the local database. This caused income and expense sync operations to fail because there were no user IDs available to sync data for. The sync managers would find an empty or null list of users, resulting in no data synchronization on the initial launch.

## Solution
Implemented a comprehensive user synchronization system that:
1. Fetches all users from Firebase Firestore during app initialization in admin mode
2. Stores users in the local database
3. Provides user IDs to income and expense sync managers
4. Ensures data is available for sync operations immediately after initialization

## Components Modified/Created

### 1. UserRepository Interface
**File:** `domain/repository/UserRepository.kt`

**New Methods:**
- `syncAllUsersFromFirebase()`: Fetches all users from Firebase and stores them locally
- `getAllLocalUsers()`: Retrieves all users from local database
- `insertUsers(users: List<UserEntity>)`: Batch insert multiple users

### 2. UserRepositoryImpl
**File:** `data/repositories/UserRepositoryImpl.kt`

**Changes:**
- Added `FirebaseFirestore` dependency injection
- Implemented `syncAllUsersFromFirebase()` to fetch users from Firestore collection "users"
- Implemented `getAllLocalUsers()` and `insertUsers()` methods
- Uses UserMapper to convert Firestore data to UserEntity

### 3. UserDao
**File:** `data/local/dao/UserDao.kt`

**New Methods:**
- `insertUsers(users: List<UserEntity>)`: Batch insert with REPLACE strategy
- `getAllUsersSync()`: Synchronous method to get all users (non-Flow)

### 4. UserMapper
**File:** `data/mappers/UserMapper.kt`

**New Extension Functions:**
- `Map<String, Any>.toUserEntity(userId: String)`: Converts Firestore document to UserEntity
- `UserEntity.toUser()`: Converts entity to domain model
- `User.toEntity()`: Converts domain model to entity
- `UserEntity.toFirestoreMap()`: Converts entity to Firestore map

**Features:**
- Handles Timestamp and Long conversions for date fields
- Provides null safety and default values
- Supports all user fields including optional ones (firstName, lastName, profilePictureUrl, etc.)

### 5. UserSyncManager (NEW)
**File:** `data/remote/sync/UserSyncManager.kt`

**Purpose:** Manages synchronization of users between Firebase Firestore and local database.

**Key Methods:**

#### `syncAllUsers(forceRefresh: Boolean = false): SyncResult`
- Fetches all users from Firebase Firestore
- Stores them in local database using batch insert
- Skips sync if users already exist locally (unless forceRefresh = true)
- Returns SyncResult with list of synced users or error message

#### `syncUser(userId: String): SyncResult`
- Syncs a single user by ID
- Useful for adding new users without full refresh

#### `getAllLocalUserIds(): List<String>`
- Returns list of all user IDs from local database
- Used by IncomeSyncManager and ExpenseSyncManager in admin mode

#### `getAllLocalUsers(): List<UserEntity>`
- Returns all users from local database
- Provides complete user information when needed

#### `hasLocalUsers(): Boolean`
- Checks if local database has any users
- Useful for determining if initial sync is needed

#### `initializeAdminSync(userRole: Role): Boolean`
- Convenience method for app initialization
- Automatically syncs users if role is ADMIN
- Returns true if sync was performed, false otherwise

**SyncResult Sealed Class:**
```kotlin
sealed class SyncResult {
    data class Success(val users: List<UserEntity>) : SyncResult()
    data class Error(val message: String) : SyncResult()
}
```

## Integration with Income/Expense Sync

### Current Behavior
Both `IncomeSyncManager` and `ExpenseSyncManager` have methods:
- `getAllUserIdsWithUnsyncedIncomes()`
- `getAllUserIdsWithUnsyncedExpenses()`

These methods query the local database for user IDs that have unsynced data.

### Enhanced Behavior for Admin Mode
When admin launches the app:

1. **App Initialization:**
   ```kotlin
   // In your initialization code (e.g., MainActivity, Application class)
   if (userRole == Role.ADMIN) {
       userSyncManager.initializeAdminSync(userRole)
   }
   ```

2. **Users Available in Local Database:**
   - All users from Firebase are now in local database
   - UserDao.getAllUsersSync() returns complete list

3. **Income/Expense Sync:**
   ```kotlin
   // In IncomeSyncManager/ExpenseSyncManager
   private suspend fun getAllUserIdsWithUnsyncedIncomes(): Set<String> {
       return incomeDao.getAllUnsyncedUserIds().toSet()
   }
   ```
   - This now returns actual user IDs because users exist in local database
   - Even on first launch, sync operations can find user IDs from the synced users

## Usage Example

### During App Startup (Admin Mode)
```kotlin
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var userSyncManager: UserSyncManager
    
    @Inject
    lateinit var incomeSyncManager: IncomeSyncManager
    
    @Inject
    lateinit var expenseSyncManager: ExpenseSyncManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            val userRole = getUserRole() // Get from preferences/auth
            
            // Step 1: Sync users if admin
            if (userRole == Role.ADMIN) {
                val syncSuccess = userSyncManager.initializeAdminSync(userRole)
                
                if (syncSuccess) {
                    // Step 2: Now sync income/expense data
                    incomeSyncManager.uploadLocalIncomes(
                        authenticatedUserId = getCurrentUserId(),
                        userRole = userRole,
                        targetUserIds = null // null means all users
                    )
                    
                    expenseSyncManager.uploadLocalExpenses(
                        authenticatedUserId = getCurrentUserId(),
                        userRole = userRole,
                        targetUserIds = null
                    )
                }
            }
        }
    }
}
```

### Manual User Sync
```kotlin
// Force refresh all users
val result = userSyncManager.syncAllUsers(forceRefresh = true)
when (result) {
    is UserSyncManager.SyncResult.Success -> {
        Log.d(TAG, "Synced ${result.users.size} users")
    }
    is UserSyncManager.SyncResult.Error -> {
        Log.e(TAG, "Sync failed: ${result.message}")
    }
}
```

### Get User IDs for Sync
```kotlin
// Get all local user IDs
val userIds = userSyncManager.getAllLocalUserIds()

// Use for targeted sync
incomeSyncManager.uploadLocalIncomes(
    authenticatedUserId = currentUserId,
    userRole = Role.ADMIN,
    targetUserIds = userIds.toSet()
)
```

## Dependency Injection

Ensure UserSyncManager is provided in your Hilt/Dagger module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    
    @Provides
    @Singleton
    fun provideUserSyncManager(
        firestore: FirebaseFirestore,
        userDao: UserDao,
        userRepository: UserRepository
    ): UserSyncManager {
        return UserSyncManager(firestore, userDao, userRepository)
    }
}
```

## Flow Diagram

```
App Launch (Admin Mode)
    |
    v
UserSyncManager.initializeAdminSync(Role.ADMIN)
    |
    v
Check if users exist locally
    |
    +-- Yes: Skip sync
    |
    +-- No: Fetch from Firebase
            |
            v
        Firebase Firestore "users" collection
            |
            v
        Convert documents to UserEntity
            |
            v
        Batch insert into local database
            |
            v
        Return Success with user list
    |
    v
IncomeSyncManager.uploadLocalIncomes()
    |
    v
getAllUserIdsWithUnsyncedIncomes()
    |
    v
Query local database for unsynced incomes
    |
    v
Get distinct user IDs (now available!)
    |
    v
Sync incomes for each user ID
```

## Benefits

1. **No Empty Sync:** Admin mode always has users available for sync operations
2. **Fast Initialization:** User sync happens once and is cached locally
3. **Offline Support:** Users remain in local database even offline
4. **Scalable:** Batch operations ensure efficient handling of many users
5. **Error Handling:** Comprehensive error handling and logging
6. **Flexible:** Supports both full sync and individual user sync

## Testing Considerations

1. **Test Initial Launch:** Verify users are synced on first admin login
2. **Test Subsequent Launches:** Verify sync is skipped when users exist
3. **Test Force Refresh:** Verify forceRefresh parameter works correctly
4. **Test Error Handling:** Simulate Firebase errors and verify graceful handling
5. **Test Non-Admin:** Verify non-admin users don't trigger user sync

## Notes

- User sync uses `OnConflictStrategy.REPLACE` to handle updates
- All sync operations are suspend functions for coroutine compatibility
- Logging is comprehensive for debugging and monitoring
- UserSyncManager is a Singleton to prevent duplicate instances

## Future Enhancements

1. Add incremental sync based on timestamp
2. Add user deletion sync
3. Add user update notifications
4. Implement sync conflict resolution
5. Add progress callbacks for UI updates

---

**Date:** January 9, 2026  
**Version:** 1.0  
**Author:** Development Team
