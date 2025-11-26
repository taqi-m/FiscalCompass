package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.model.CategoryEntity
import com.fiscal.compass.data.local.model.PersonEntity
import com.fiscal.compass.domain.model.PersonType
import com.fiscal.compass.data.managers.SyncTimestampManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toCategoryEntity
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toFirestoreMap
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
            // Separate categories into parents and children
            val (parents, children) = unsyncedCategories.partition { it.parentCategoryId == null }
            val globalCategoriesRef = firestore.collection("globalCategories")
            val currentSyncTime = System.currentTimeMillis()

            // Upload parent categories first
            parents.forEach { category ->
                try {
                    Log.d(TAG, "Uploading parent category ${category.name} | isDeleted: ${category.isDeleted}")
                    uploadCategory(category, globalCategoriesRef, syncTime = currentSyncTime)
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading parent category ${category.name}", e)
                }
            }

            // Then upload child categories
            children.forEach { category ->
                try {
                    val parentId = category.parentCategoryId
                    val parentFirestoreId = parentId?.let { categoryDao.getFirestoreId(it) }

                    if (parentId != null && parentFirestoreId == null) {
                        Log.w(TAG, "Skipping category ${category.name} as parent sync is pending")
                        return@forEach
                    }
                    Log.d(TAG, "Uploading child category ${category.name} | isDeleted: ${category.isDeleted}")
                    uploadCategory(
                        category = category,
                        collectionRef = globalCategoriesRef,
                        parentFirestoreId = parentFirestoreId,
                        syncTime = currentSyncTime
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading child category ${category.name}", e)
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
        parentFirestoreId: String? = null,
        syncTime: Long
    ) {
        try {
            val categoryDto = category.toDto()
            var firestoreDocId = category.firestoreId

            if (firestoreDocId == null) {
                Log.d(TAG, "Category ${category.name} has no Firestore ID, generating new one")
                firestoreDocId = collectionRef.document().id
            }

            Log.d(
                TAG,
                "Uploading category '${category.name}' | isDeleted: ${category.isDeleted}  with localId=${category.localId}, firestoreId=$firestoreDocId"
            )

            val categoryMap = categoryDto.toFirestoreMap(
                firestoreId = firestoreDocId,
                firestoreIdOfParent = parentFirestoreId,
                syncTime = syncTime
            )

            collectionRef.document(firestoreDocId).set(categoryMap).await()

            // Update local entity with sync status
            categoryDao.update(
                category.copy(
                    firestoreId = firestoreDocId,
                    parentCategoryFirestoreId = parentFirestoreId,
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

            // Process parents first, then children to maintain referential integrity
            val (categoriesWithoutParent, categoriesWithParent) = globalCategories.documents.partition {
                val parentId = it.data?.get("parentCategoryFirestoreId") as String?
                parentId.isNullOrEmpty()
            }

            Log.d(
                TAG,
                "Categories without parent: ${categoriesWithoutParent.size}, with parent: ${categoriesWithParent.size}"
            )

            // Process parent categories
            categoriesWithoutParent.forEach { doc ->
                try {
                    processRemoteCategory(doc.data ?: return@forEach)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing parent category from Firestore", e)
                }
            }

            // Process child categories
            categoriesWithParent.forEach { doc ->
                try {
                    processRemoteCategory(doc.data ?: return@forEach)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing child category from Firestore", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.CATEGORIES)
        } catch (e: Exception) {
            Log.e(TAG, "Error in downloadRemoteCategories", e)
            throw e
        }
    }

    private suspend fun processRemoteCategory(categoryData: Map<String, Any>) {
        val parsedCategory = categoryData.toCategoryEntity()
        val existingCategory = parsedCategory.firestoreId?.let {
            categoryDao.getCategoryByFirestoreId(it)
        }

        if (existingCategory == null) {
            // New category from remote - handle parent relationship
            val parentFirestoreId = parsedCategory.parentCategoryFirestoreId
            val parentId = if (!parentFirestoreId.isNullOrEmpty()) {
                getParentCategoryIdFromFirestoreId(parentFirestoreId)
            } else null

            if (parentFirestoreId != null && parentFirestoreId.isNotEmpty() && parentId == null) {
                Log.w(
                    TAG,
                    "Parent category with Firestore ID $parentFirestoreId not found locally, skipping ${parsedCategory.name}"
                )
                return
            }

            categoryDao.insert(parsedCategory.copy(parentCategoryId = parentId))
            return
        }

        // Update only if remote version is newer and not conflicting with local changes
        val remoteUpdateTime = parsedCategory.updatedAt
        val localUpdateTime = existingCategory.updatedAt

        if (remoteUpdateTime > localUpdateTime) {
            // Handle parent relationship for updates
            val parentFirestoreId = parsedCategory.parentCategoryFirestoreId
            val parentId = if (!parentFirestoreId.isNullOrEmpty()) {
                getParentCategoryIdFromFirestoreId(parentFirestoreId)
            } else null

            categoryDao.update(
                parsedCategory.copy(
                    categoryId = existingCategory.categoryId,
                    parentCategoryId = parentId,
                    isSynced = true,
                    needsSync = false
                )
            )
        }
    }

    private suspend fun getParentCategoryIdFromFirestoreId(parentFirestoreId: String?): Long? {
        if (parentFirestoreId == null) return null
        val parentCategory = categoryDao.getCategoryByFirestoreId(parentFirestoreId)
        return parentCategory?.categoryId
    }

    // Helper method to create PersonEntity from Firestore data
    private fun createPersonFromFirestore(
        firestoreId: String,
        data: Map<String, Any>
    ): PersonEntity {
        // Adjust this based on your actual PersonEntity structure
        return PersonEntity(
            personId = 0,
            firestoreId = firestoreId,
            localId = data["localId"] as? String ?: firestoreId,
            name = data["name"] as? String ?: "",
            personType = PersonType.fromString(data["personType"] as? String ?: ""),
            contact = data["contact"] as? String,
            isSynced = true,
            needsSync = false,
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    companion object {
        const val TAG = "CategorySyncManager"
    }
}