package com.app.uniqueplant.data.sources.remote.sync

import android.util.Log
import com.app.uniqueplant.data.manager.SyncDependencyManager
import com.app.uniqueplant.data.manager.SyncTimestampManager
import com.app.uniqueplant.data.manager.SyncType
import com.app.uniqueplant.data.model.CategoryEntity
import com.app.uniqueplant.data.model.ExpenseEntity
import com.app.uniqueplant.data.model.IncomeEntity
import com.app.uniqueplant.data.model.PersonEntity
import com.app.uniqueplant.data.model.PersonType
import com.app.uniqueplant.data.sources.local.dao.CategoryDao
import com.app.uniqueplant.data.sources.local.dao.ExpenseDao
import com.app.uniqueplant.data.sources.local.dao.IncomeDao
import com.app.uniqueplant.data.sources.local.dao.PersonDao
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class EnhancedSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val categoryDao: CategoryDao,
    private val personDao: PersonDao,
    private val auth: FirebaseAuth,
    private val timestampManager: SyncTimestampManager,
    private val dependencyManager: SyncDependencyManager
) {

    suspend fun syncAllData() {

        syncCategories()
        syncExpenses()
        syncIncomes()
    }

    suspend fun syncExpenses() {
        val userId = auth.currentUser?.uid ?: return

        // Check dependencies before syncing (if you have access to dependencyManager)
        if (!dependencyManager.canSync(SyncType.EXPENSES, userId)) {
            Log.w("Sync", "Cannot sync expenses: dependencies not satisfied")
            return
        }

        try {
            // 1. Upload new/modified local expenses
            uploadLocalExpenses(userId)

            // 2. Download remote expenses
            downloadRemoteExpenses(userId)

            //3. Update last sync timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES)

        } catch (e: Exception) {
            Log.e("Sync", "Error syncing data", e)
        }
    }

    suspend fun syncIncomes() {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Upload new/modified local incomes
            Log.d(
                "Sync",
                "Starting income sync for user $userId"
            )
            uploadLocalIncomes(userId)
            // 2. Download remote incomes
            downloadRemoteIncomes(userId)

            //3. Update last sync timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES)

        } catch (e: Exception) {
            Log.e("Sync", "Error syncing data", e)
        }
    }

    suspend fun syncPersons() {
        // Sync persons data
    }

    suspend fun syncCategories() {
        downloadRemoteCategories()
    }

    private suspend fun downloadRemoteCategories() {
        val userId = auth.currentUser?.uid ?: return
        val lastSyncTime = timestampManager.getLastSyncTimestamp(SyncType.CATEGORIES, userId)

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("categories")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d("Sync", "Found ${snapshot.documents.size} remote categories to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            val categoryData = doc.data ?: return@forEach
            val remoteCategory = categoryFromFirestore(categoryData, doc.id)

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteCategory.updatedAt)

            // Check if category already exists locally
            val existingCategory = categoryDao.getCategoryByLocalId(remoteCategory.localId)

            if (existingCategory != null) {
                // Update existing category
                categoryDao.update(remoteCategory.copy(categoryId = existingCategory.categoryId))
            } else {
                // Insert new category
                categoryDao.insert(remoteCategory)
            }

            processedCount++
        }

        Log.d("Sync", "Processed $processedCount remote categories")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.CATEGORIES, latestRemoteTimestamp)
        }
    }

    private suspend fun uploadLocalExpenses(userId: String) {
        val unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId)

        if (unsyncedExpenses.isEmpty()) {
            Log.d("Sync", "No local expenses to upload")
            return
        }

        Log.d("Sync", "Uploading ${unsyncedExpenses.size} local expenses")

        val userExpensesRef = firestore.collection("users")
            .document(userId)
            .collection("expenses")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0

        unsyncedExpenses.forEach { expense ->
            val firestoreDocId = expense.firestoreId ?: expense.localId

            val expenseData = mapOf(
                "localId" to expense.localId,
                "amount" to expense.amount,
                "description" to expense.description,
                "date" to Timestamp(Date(expense.date)),
                "categoryLocalId" to categoryDao.getCategoryLocalId(expense.categoryId),
                "categoryFirestoreId" to categoryDao.getCategoryFirestoreId(expense.categoryId),
                "personLocalId" to personDao.getPersonLocalId(expense.personId),
                "personFirestoreId" to personDao.getPersonFirestoreId(expense.personId),
                "paymentMethod" to expense.paymentMethod,
                "location" to expense.location,
                "isRecurring" to expense.isRecurring,
                "recurringFrequency" to expense.recurringFrequency,
                "createdAt" to Timestamp(Date(expense.createdAt)),
                "updatedAt" to Timestamp(Date(expense.updatedAt))
            )

            val docRef = userExpensesRef.document(firestoreDocId)
            batch.set(docRef, expenseData)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                batch = firestore.batch()
                batchCount = 0
            }

        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
        }

        // Update local sync status
        unsyncedExpenses.forEach { expense ->
            expenseDao.updateSyncStatus(
                expenseId = expense.expenseId,
                firestoreId = expense.firestoreId ?: expense.localId,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )
        }

        Log.d("Sync", "Successfully uploaded ${unsyncedExpenses.size} expenses")
    }

    private suspend fun downloadRemoteExpenses(userId: String, isInitialization: Boolean = false) {
        val lastSyncTime = if (isInitialization) {
            0L // Download all data during initialization
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.EXPENSES, userId)
        }

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d("Sync", "Found ${snapshot.documents.size} remote expenses to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            val localId = doc.getString("categoryLocalId")
            if (localId.isNullOrEmpty()) {
                // Skip documents without a valid localId
                return@forEach
            }
            val categoryId = categoryDao.getCategoryIdByLocalId(localId)
            if (categoryId == 0L || categoryId == null) {
                // Skip if category doesn't exist locally
                return@forEach
            }

            val remoteExpense = ExpenseEntity(
                expenseId = 0, // Will be auto-generated
                firestoreId = doc.id,
                localId = doc.getString("localId") ?: doc.id,
                amount = doc.getDouble("amount") ?: 0.0,
                description = doc.getString("description") ?: "",
                date = (doc.getTimestamp("date")?.toDate()?.time) ?: 0L,
                categoryId = categoryId,
                userId = userId,
                personId = personDao.getPersonIdByLocalId(doc.getString("personLocalId")),
                paymentMethod = doc.getString("paymentMethod"),
                location = doc.getString("location"),
                isRecurring = doc.getBoolean("isRecurring") ?: false,
                recurringFrequency = doc.getString("recurringFrequency"),
                createdAt = (doc.getTimestamp("createdAt")?.toDate()?.time)
                    ?: System.currentTimeMillis(),
                updatedAt = (doc.getTimestamp("updatedAt")?.toDate()?.time)
                    ?: System.currentTimeMillis(),
                isDeleted = false,
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteExpense.updatedAt)

            // Check if expense already exists locally
            val existingExpense = expenseDao.getExpenseByLocalId(remoteExpense.localId)

            if (existingExpense != null) {
                // Update existing expense
                val resolvedExpense = resolveConflict(existingExpense, remoteExpense)
                expenseDao.update(resolvedExpense)
            } else {
                // Insert new expense
                expenseDao.insert(remoteExpense)
            }

            processedCount++
        }

        Log.d("Sync", "Processed $processedCount remote expenses")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES, latestRemoteTimestamp)
        }

    }

    private suspend fun uploadLocalIncomes(userId: String) {
        val unsyncedIncomes = incomeDao.getUnsyncedIncomes(userId)

        if (unsyncedIncomes.isEmpty()) {
            Log.d("Sync", "No local incomes to upload")
            return
        }

        Log.d("Sync", "Uploading ${unsyncedIncomes.size} local incomes")

        val userIncomesRef = firestore.collection("users")
            .document(userId)
            .collection("incomes")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0

        unsyncedIncomes.forEach { income ->
            val firestoreDocId = income.firestoreId ?: income.localId

            val incomeData = mapOf(
                "localId" to income.localId,
                "amount" to income.amount,
                "description" to income.description,
                "date" to Timestamp(Date(income.date)),
                "categoryLocalId" to categoryDao.getCategoryLocalId(income.categoryId),
                "categoryFirestoreId" to categoryDao.getCategoryFirestoreId(income.categoryId),
                "personLocalId" to personDao.getPersonLocalId(income.personId),
                "personFirestoreId" to personDao.getPersonFirestoreId(income.personId),
                "isRecurring" to income.isRecurring,
                "recurringFrequency" to income.recurringFrequency,
                "createdAt" to Timestamp(Date(income.createdAt)),
                "updatedAt" to Timestamp(Date(income.updatedAt))
            )

            val docRef = userIncomesRef.document(firestoreDocId)
            batch.set(docRef, incomeData)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                batch = firestore.batch()
                batchCount = 0
            }

        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
        }

        // Update local sync status
        unsyncedIncomes.forEach { income ->
            incomeDao.updateSyncStatus(
                incomeId = income.incomeId,
                firestoreId = income.firestoreId ?: income.localId,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )
        }

        Log.d("Sync", "Successfully uploaded ${unsyncedIncomes.size} incomes")
    }

    private suspend fun downloadRemoteIncomes(userId: String, isInitialization: Boolean = false) {
        val lastSyncTime = if (isInitialization) {
            0L // Download all data during initialization
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.INCOMES, userId)
        }

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("incomes")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d("Sync", "Found ${snapshot.documents.size} remote incomes to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            val localId = doc.getString("categoryLocalId")
            if (localId.isNullOrEmpty()) {
                // Skip documents without a valid localId
                return@forEach
            }
            val categoryId = categoryDao.getCategoryIdByLocalId(localId)
            if (categoryId == 0L || categoryId == null) {
                // Skip if category doesn't exist locally
                return@forEach
            }

            val remoteIncome = IncomeEntity(
                incomeId = 0, // Will be auto-generated
                firestoreId = doc.id,
                localId = doc.getString("localId") ?: doc.id,
                amount = doc.getDouble("amount") ?: 0.0,
                description = doc.getString("description") ?: "",
                date = (doc.getTimestamp("date")?.toDate()?.time) ?: 0L,
                categoryId = categoryId,
                personId = personDao.getPersonIdByLocalId(doc.getString("personLocalId")),
                userId = userId,
                isRecurring = doc.getBoolean("isRecurring") ?: false,
                recurringFrequency = doc.getString("recurringFrequency"),
                createdAt = (doc.getTimestamp("createdAt")?.toDate()?.time)
                    ?: System.currentTimeMillis(),
                updatedAt = (doc.getTimestamp("updatedAt")?.toDate()?.time)
                    ?: System.currentTimeMillis(),
                isDeleted = false,
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteIncome.updatedAt)

            // Check if income already exists locally
            val existingIncome = incomeDao.getIncomeByLocalId(remoteIncome.localId)

            if (existingIncome != null) {
                // Update existing income
                val resolvedIncome = resolveIncomeConflict(existingIncome, remoteIncome)
                incomeDao.update(resolvedIncome)
            } else {
                // Insert new income
                incomeDao.insert(remoteIncome)
            }

            processedCount++
        }

        Log.d("Sync", "Processed $processedCount remote incomes")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES, latestRemoteTimestamp)
        }
    }

    suspend fun initializeCategories(userId: String) {
        Log.d("Init", "Initializing categories for user: $userId")

        try {
            // Download global categories first
            val globalCategories = firestore.collection("globalCategories")
                .get()
                .await()

            Log.d("Init", "Found ${globalCategories.documents.size} global categories")

            // Separate categories with and without parentCategoryId
            val (categoriesWithoutParent, categoriesWithParent) = globalCategories.documents.partition {
                val parentId = it.data?.get("parentCategoryFirestoreId") as String?
                parentId.isNullOrEmpty()
            }

            Log.d(
                "Init",
                "Categories without parent: ${categoriesWithoutParent.size}, with parent: ${categoriesWithParent.size}"
            )

            // Insert categories without parentCategoryId first
            categoriesWithoutParent.forEach { doc ->
                val categoryData = doc.data ?: return@forEach
                val category = categoryFromFirestore(categoryData, doc.id)
                val existingCategory = categoryDao.getCategoryByFirestoreId(doc.id)
                if (existingCategory == null) {
                    categoryDao.insert(category)
                } else {
                    categoryDao.update(category.copy(categoryId = existingCategory.categoryId))
                }
            }

            // Insert categories with parentCategoryId
            categoriesWithParent.forEach { doc ->
                val categoryData = doc.data ?: return@forEach
                val category = categoryFromFirestore(categoryData, doc.id)
                // Ensure parent category exists locally before inserting
                val parentFirestoreId = category.parentCategoryFirestoreId
                if (parentFirestoreId != null) {
                    val existingCategory = categoryDao.getCategoryByFirestoreId(doc.id)
                    if (existingCategory == null) {
                        categoryDao.insert(category.copy(parentCategoryId = getParentCategoryId(parentFirestoreId)))
                    } else {
                        categoryDao.update(
                            category.copy(
                                categoryId = existingCategory.categoryId,
                                parentCategoryId = getParentCategoryId(parentFirestoreId)
                            )
                        )
                    }
                } else {
                    Log.w(
                        "Init",
                        "Parent category with firestoreId $parentFirestoreId not found for category ${category.name}"
                    )
                }

            }

            // Also check for user-specific categories
            /*val userCategories = firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .await()

            Log.d("Init", "Found ${userCategories.documents.size} user-specific categories")

            val (userCategoriesWithParent, userCategoriesWithoutParent) = userCategories.documents.partition {
                it.data?.get("parentCategoryId") != null
            }

            // Insert user-specific categories without parentCategoryId first
            userCategoriesWithoutParent.forEach { doc ->
                val categoryData = doc.data ?: return@forEach
                val category = categoryFromFirestore(categoryData, doc.id)
                val existingCategory = categoryDao.getCategoryByFirestoreId(doc.id)
                if (existingCategory == null) {
                    categoryDao.insert(category)
                } else {
                    categoryDao.update(category.copy(categoryId = existingCategory.categoryId))
                }
            }

            // Insert user-specific categories with parentCategoryId
            userCategoriesWithParent.forEach { doc ->
                val categoryData = doc.data ?: return@forEach
                val category = categoryFromFirestore(categoryData, doc.id)
                val parentFirestoreId = category.parentCategoryFirestoreId
                if (parentFirestoreId != null) {
                    val parentCategory = categoryDao.getCategoryByFirestoreId(parentFirestoreId)
                    if (parentCategory != null) {
                        val existingCategory = categoryDao.getCategoryByFirestoreId(doc.id)
                        if (existingCategory == null) {
                            categoryDao.insert(category.copy(parentCategoryId = parentCategory.categoryId))
                        } else {
                            categoryDao.update(
                                category.copy(
                                    categoryId = existingCategory.categoryId,
                                    parentCategoryId = parentCategory.categoryId
                                )
                            )
                        }
                    } else {
                        Log.w(
                            "Init",
                            "Parent category with firestoreId $parentFirestoreId not found for user category ${category.name}"
                        )
                    }
                }
            }*/
            // Update timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.CATEGORIES)

            Log.d("Init", "Categories initialization completed")

        } catch (e: Exception) {
            Log.e("Init", "Failed to initialize categories", e)
            throw e
        }
    }

    suspend fun initializePersons(userId: String) {
        Log.d("Init", "Initializing persons for user: $userId")

        try {
            // Download user's persons
            val userPersons = firestore.collection("users")
                .document(userId)
                .collection("persons")
                .get()
                .await()

            Log.d("Init", "Found ${userPersons.documents.size} persons")

            userPersons.documents.forEach { doc ->
                val personData = doc.data ?: return@forEach

                // Create PersonEntity based on your data model
                // You'll need to adjust this based on your actual PersonEntity structure
                val person = createPersonFromFirestore(doc.id, personData)

                val existingPerson = personDao.getPersonByFirestoreId(doc.id)

                if (existingPerson == null) {
                    personDao.insert(person)
                } else {
                    personDao.update(person.copy(personId = existingPerson.personId))
                }
            }

            // Update timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.PERSONS)

            Log.d("Init", "Persons initialization completed")

        } catch (e: Exception) {
            Log.e("Init", "Failed to initialize persons", e)
            throw e
        }
    }

    suspend fun initializeExpenses(userId: String) {
        Log.d("Init", "Initializing expenses for user: $userId")

        try {
            // Use existing download method but with initialization flag
            downloadRemoteExpenses(userId, isInitialization = true)

            Log.d("Init", "Expenses initialization completed")

        } catch (e: Exception) {
            Log.e("Init", "Failed to initialize expenses", e)
            throw e
        }
    }

    suspend fun initializeIncomes(userId: String) {
        Log.d("Init", "Initializing incomes for user: $userId")

        try {
            // Use existing download method but with initialization flag
            downloadRemoteIncomes(userId, isInitialization = true)

            Log.d("Init", "Incomes initialization completed")

        } catch (e: Exception) {
            Log.e("Init", "Failed to initialize incomes", e)
            throw e
        }
    }

    private suspend fun categoryFromFirestore(data: Map<String, Any>, docId: String): CategoryEntity {
        return CategoryEntity(
            categoryId = 0, // Will be auto-generated
            firestoreId = docId,
            localId = data["localId"] as? String ?: docId,
            name = data["name"] as String,
            color = (data["color"] as? Long)?.toInt() ?: 0xFF000000.toInt(),
            isExpenseCategory = data["isExpenseCategory"] == 1L,
            icon = data["icon"] as? String,
            description = data["description"] as? String,
            expectedPersonType = data["expectedPersonType"] as? String,
            parentCategoryId = getParentCategoryId(data["parentCategoryFirestoreId"] as? String),
            parentCategoryFirestoreId = data["parentCategoryFirestoreId"] as? String,
            createdAt = (data["createdAt"] as Timestamp).toDate().time,
            updatedAt = (data["updatedAt"] as Timestamp).toDate().time,
            isSynced = true,
            needsSync = false
        )
    }

    private suspend fun getParentCategoryId(parentFirestoreId: String?): Long? {
        if (parentFirestoreId == null) return null
        val parentCategory = categoryDao.getCategoryByFirestoreId(parentFirestoreId)
        return parentCategory?.categoryId
    }

    // Helper method to create PersonEntity from Firestore data
    private fun createPersonFromFirestore(firestoreId: String, data: Map<String, Any>): PersonEntity {
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

    private fun resolveIncomeConflict(existingIncome: IncomeEntity, remoteIncome: IncomeEntity): IncomeEntity {
        // Simple conflict resolution: keep the most recently updated record
        if (remoteIncome.updatedAt > existingIncome.updatedAt) {
            return remoteIncome.copy(incomeId = existingIncome.incomeId)
        }
        return existingIncome
    }

    private fun resolveConflict(existingExpense: ExpenseEntity, remoteExpense: ExpenseEntity): ExpenseEntity {
        // Simple conflict resolution: keep the most recently updated record
        if (remoteExpense.updatedAt > existingExpense.updatedAt) {
            return remoteExpense.copy(expenseId = existingExpense.expenseId)
        }
        return existingExpense
    }
}