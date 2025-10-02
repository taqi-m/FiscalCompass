package com.app.uniqueplant.data.remote.sync

import android.util.Log
import com.app.uniqueplant.data.managers.SyncDependencyManager
import com.app.uniqueplant.data.managers.SyncTimestampManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.local.model.CategoryEntity
import com.app.uniqueplant.data.local.model.ExpenseEntity
import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.data.local.model.PersonEntity
import com.app.uniqueplant.data.local.model.PersonType
import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.data.local.dao.CategoryDao
import com.app.uniqueplant.data.local.dao.ExpenseDao
import com.app.uniqueplant.data.local.dao.IncomeDao
import com.app.uniqueplant.data.local.dao.PersonDao
import com.app.uniqueplant.data.mappers.toDto
import com.app.uniqueplant.data.mappers.toFirestoreMap
import com.app.uniqueplant.data.mappers.toCategoryEntity
import com.app.uniqueplant.data.mappers.toPersonEntity
import com.app.uniqueplant.domain.usecase.rbac.CheckPermissionUseCase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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
    private val dependencyManager: SyncDependencyManager,
    private val checkPermissionUseCase: CheckPermissionUseCase
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
            Log.w(TAG, "Cannot sync expenses: dependencies not satisfied")
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
            Log.e(TAG, "Error syncing data", e)
        }
    }

    suspend fun syncIncomes() {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Upload new/modified local incomes
            Log.d(TAG, "Starting income sync for user $userId")
            uploadLocalIncomes(userId)
            // 2. Download remote incomes
            downloadRemoteIncomes(userId)

            //3. Update last sync timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES)

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing data", e)
        }
    }

    suspend fun syncPersons() {
        if (checkPermissionUseCase(Permission.ADD_PERSON)) {
            uploadLocalPersons()
        }
        dowloadRemotePersons()
    }

    suspend fun syncCategories() {
        if (checkPermissionUseCase(Permission.ADD_CATEGORY)) {
            uploadLocalCategories()
        }
        downloadRemoteCategories()
    }

    private suspend fun uploadLocalPersons() {
        val unsyncedPersons = personDao.getUnsyncedPersons()
        if (unsyncedPersons.isEmpty()) {
            Log.d(TAG, "No local persons to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedPersons.size} local persons")

        try {
            val globalPersonRef = firestore.collection("globalPersons")
            val currentSyncTime = System.currentTimeMillis()
            unsyncedPersons.forEach { person ->
                try {
                    val personDto = person.toDto()
                    var firestoreDocId = person.firestoreId

                    if (firestoreDocId == null) {
                        Log.d(TAG, "Person ${person.name} has no Firestore ID, generating new one")
                        firestoreDocId = globalPersonRef.document().id
                    }

                    Log.d(TAG, "Uploading person '${person.name}' with firestoreId=$firestoreDocId")

                    val personMap = personDto.toFirestoreMap(
                        firestoreId = firestoreDocId,
                        syncTime = currentSyncTime
                    )

                    globalPersonRef.document(firestoreDocId).set(personMap).await()

                    // Update local entity with sync status
                    personDao.update(
                        person.copy(
                            firestoreId = firestoreDocId,
                            isSynced = true,
                            needsSync = false,
                            lastSyncedAt = currentSyncTime
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading person ${person.name}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in uploadLocalPersons", e)
            throw e
        }
    }

    private suspend fun dowloadRemotePersons() {
        val lastSyncTime = timestampManager.getPersonsLastSyncTimestamp()

        try {
            // Download global persons that were updated after our last sync
            val globalPersons = firestore.collection("globalPersons")
                .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
                .get()
                .await()

            Log.d(TAG, "Found ${globalPersons.documents.size} remote persons to sync")

            globalPersons.documents.forEach { doc ->
                try {
                    processRemotePerson(doc.data ?: return@forEach)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing person from Firestore", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.PERSONS)
        } catch (e: Exception) {
            Log.e(TAG, "Error in downloadRemotePersons", e)
            throw e
        }
    }

    private suspend fun processRemotePerson(personData: Map<String, Any>) {
        val parsedPerson = personData.toPersonEntity()
        val existingPerson = parsedPerson.firestoreId?.let {
            personDao.getPersonByFirestoreId(it)
        }

        if (existingPerson == null) {
            personDao.insert(parsedPerson)
            return
        }

        val remoteUpdateTime = parsedPerson.updatedAt
        val localUpdateTime = existingPerson.updatedAt

        if (remoteUpdateTime > localUpdateTime) {
            personDao.update(
                parsedPerson.copy(
                    personId = existingPerson.personId,
                    isSynced = true,
                    needsSync = false,
                )
            )
        }
    }

    private suspend fun uploadLocalCategories() {
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
                "Uploading category '${category.name}' with localId=${category.localId}, firestoreId=$firestoreDocId"
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

    private suspend fun downloadRemoteCategories() {
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

    private suspend fun uploadLocalExpenses(userId: String) {
        val unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId)

        if (unsyncedExpenses.isEmpty()) {
            Log.d(TAG, "No local expenses to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedExpenses.size} local expenses")

        val userExpensesRef = firestore.collection("users")
            .document(userId)
            .collection("expenses")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0

        unsyncedExpenses.forEach { expense ->
            val firestoreDocId = userExpensesRef.document().id

            val expenseData = mapOf(
                "firestoreId" to firestoreDocId,
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

        Log.d(TAG, "Successfully uploaded ${unsyncedExpenses.size} expenses")
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

        Log.d(TAG, "Found ${snapshot.documents.size} remote expenses to sync")

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

        Log.d(TAG, "Processed $processedCount remote expenses")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES, latestRemoteTimestamp)
        }

    }

    private suspend fun uploadLocalIncomes(userId: String) {
        val unsyncedIncomes = incomeDao.getUnsyncedIncomes(userId)

        if (unsyncedIncomes.isEmpty()) {
            Log.d(TAG, "No local incomes to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedIncomes.size} local incomes")

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

        Log.d(TAG, "Successfully uploaded ${unsyncedIncomes.size} incomes")
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

        Log.d(TAG, "Found ${snapshot.documents.size} remote incomes to sync")

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

        Log.d(TAG, "Processed $processedCount remote incomes")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES, latestRemoteTimestamp)
        }
    }

    suspend fun initializeCategories(userId: String) {
        Log.d(TAG, "Initializing categories")

        try {
            downloadRemoteCategories()

            Log.d(TAG, "Categories initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize categories", e)
            throw e
        }
    }

    suspend fun initializePersons(userId: String) {
        Log.d(TAG, "Initializing persons")

        try {
            dowloadRemotePersons()
            Log.d(TAG, "Persons initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize persons", e)
            throw e
        }

        /*try {
            // Download user's persons
            val userPersons = firestore.collection("users")
                .document(userId)
                .collection("persons")
                .get()
                .await()

            Log.d(TAG, "Found ${userPersons.documents.size} persons")

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

            Log.d(TAG, "Persons initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize persons", e)
            throw e
        }*/
    }

    suspend fun initializeExpenses(userId: String) {
        Log.d(TAG, "Initializing expenses for user: $userId")

        try {
            // Use existing download method but with initialization flag
            downloadRemoteExpenses(userId, isInitialization = true)

            Log.d(TAG, "Expenses initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize expenses", e)
            throw e
        }
    }

    suspend fun initializeIncomes(userId: String) {
        Log.d(TAG, "Initializing incomes for user: $userId")

        try {
            // Use existing download method but with initialization flag
            downloadRemoteIncomes(userId, isInitialization = true)

            Log.d(TAG, "Incomes initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize incomes", e)
            throw e
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

    private fun resolveIncomeConflict(
        existingIncome: IncomeEntity,
        remoteIncome: IncomeEntity
    ): IncomeEntity {
        // Simple conflict resolution: keep the most recently updated record
        if (remoteIncome.updatedAt > existingIncome.updatedAt) {
            return remoteIncome.copy(incomeId = existingIncome.incomeId)
        }
        return existingIncome
    }

    private fun resolveConflict(
        existingExpense: ExpenseEntity,
        remoteExpense: ExpenseEntity
    ): ExpenseEntity {
        // Simple conflict resolution: keep the most recently updated record
        if (remoteExpense.updatedAt > existingExpense.updatedAt) {
            return remoteExpense.copy(expenseId = existingExpense.expenseId)
        }
        return existingExpense
    }

    companion object {
        const val TAG = "SyncManager"
    }
}