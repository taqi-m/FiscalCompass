package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.remote.model.CategoryDto
import com.fiscal.compass.domain.sync.SyncTimestampManager
import com.fiscal.compass.data.mappers.toCategoryDto
import com.fiscal.compass.data.mappers.toCategoryEntity
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.domain.model.sync.SyncType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class CategorySyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val categoryDao: CategoryDao,
){
    suspend fun uploadLocalCategories() {
        val unsyncedCategories = categoryDao.getUnsyncedCategories()
        if (unsyncedCategories.isEmpty()) {
            Log.d(TAG, "No local categories to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedCategories.size} local categories")

        try {
            val globalCategoriesRef = firestore.collection("globalCategories")
            val currentSyncTime = System.currentTimeMillis()

            unsyncedCategories.forEach { category ->
                try {
                    Log.d(TAG, "Uploading category ${category.name} | isDeleted: ${category.isDeleted}")
                    uploadCategory(
                        category = category,
                        collectionRef = globalCategoriesRef,
                        syncTime = currentSyncTime
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading category ${category.name}", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.CATEGORIES)
        } catch (e: Exception) {
            Log.e(TAG, "Error in uploadLocalCategories", e)
            throw e
        }
    }

    private suspend fun uploadCategory(
        category: CategoryEntity,
        collectionRef: CollectionReference,
        syncTime: Long
    ) {
        try {
            val categoryDto = category.toDto()

            Log.d(
                TAG,
                "Uploading category '${category.name}' | isDeleted: ${category.isDeleted} with categoryId=${category.categoryId}"
            )

            // Use categoryId as Firestore document ID with stable field names
            collectionRef.document(category.categoryId).set(categoryDto.toFirestoreMap(syncTime)).await()

            // Update local entity with sync status
            categoryDao.update(
                category.copy(
                    isSynced = true,
                    needsSync = false,
                    lastSyncedAt = syncTime
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading category ${category.name}", e)
            throw e
        }
    }

    suspend fun downloadRemoteCategories() {
        val lastSyncTime = timestampManager.getCategoriesLastSyncTimestamp()

        try {
            // Download global categories that were updated after our last sync
            val globalCategories = firestore.collection("globalCategories")
                .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
                .get()
                .await()

            Log.d(TAG, "Found ${globalCategories.documents.size} remote categories to sync")

            // Process all categories using direct DTO deserialization
            globalCategories.documents.forEach { doc ->
                try {
                    val categoryDto = doc.toCategoryDto()
                    if (categoryDto != null) {
                        processRemoteCategory(categoryDto)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing category from Firestore", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.CATEGORIES)
        } catch (e: Exception) {
            Log.e(TAG, "Error in downloadRemoteCategories", e)
            throw e
        }
    }

    private suspend fun processRemoteCategory(categoryDto: CategoryDto) {
        // Convert DTO to entity using mapper
        val parsedCategory = categoryDto.toCategoryEntity()
        val existingCategory = categoryDao.getCategoryById(parsedCategory.categoryId)

        if (existingCategory == null) {
            // New category from remote - insert directly
            categoryDao.insert(parsedCategory)
            return
        }

        // Update only if remote version is newer
        val remoteUpdateTime = parsedCategory.updatedAt
        val localUpdateTime = existingCategory.updatedAt

        if (remoteUpdateTime > localUpdateTime) {
            categoryDao.update(
                parsedCategory.copy(
                    isSynced = true,
                    needsSync = false
                )
            )
        }
    }


    companion object {
        const val TAG = "CategorySyncManager"
    }
}