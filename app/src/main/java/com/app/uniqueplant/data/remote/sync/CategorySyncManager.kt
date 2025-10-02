package com.app.uniqueplant.data.remote.sync

import android.util.Log
import com.app.uniqueplant.data.local.model.CategoryEntity
import com.app.uniqueplant.data.local.dao.CategoryDao
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CategorySyncManager(
    private val firestore: FirebaseFirestore,
    private val categoryDao: CategoryDao
) {

    suspend fun syncGlobalCategories() {
        try {
            val globalCategories = firestore.collection("globalCategories")
                .get()
                .await()

            globalCategories.documents.forEach { doc ->
                val categoryData = doc.data ?: return@forEach

                val category = CategoryEntity(
                    categoryId = 0, // Will be auto-generated
                    firestoreId = doc.id,
                    localId = categoryData["localId"] as? String ?: UUID.randomUUID().toString(),
                    name = categoryData["name"] as String,
                    color = (categoryData["color"] as Long).toInt(),
                    isExpenseCategory = categoryData["isExpenseCategory"] as Boolean,
                    icon = categoryData["icon"] as? String,
                    description = categoryData["description"] as? String,
                    expectedPersonType = categoryData["expectedPersonType"] as? String,
                    parentCategoryFirestoreId = categoryData["parentCategoryId"] as? String,
                    createdAt = (categoryData["createdAt"] as Timestamp).toDate().time,
                    updatedAt = (categoryData["updatedAt"] as Timestamp).toDate().time,
                    isSynced = true,
                    needsSync = false
                )

                // Insert or update local category
                val existingCategory = categoryDao.getCategoryByFirestoreId(doc.id)

                if (existingCategory == null) {
                    categoryDao.insert(category)
                } else {
                    categoryDao.update(
                        category.copy(categoryId = existingCategory.categoryId)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CategorySync", "Error syncing categories", e)
        }
    }
}