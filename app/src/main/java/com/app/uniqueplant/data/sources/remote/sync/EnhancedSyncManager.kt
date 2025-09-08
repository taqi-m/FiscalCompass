package com.app.uniqueplant.data.sources.remote.sync

import android.util.Log
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
    private val auth: FirebaseAuth
) {

    suspend fun syncAllData() {
        syncExpenses()
        syncIncomes()
    }

    suspend fun syncExpenses() {
        val userId = auth.currentUser?.uid ?: return

        try {
            // 1. Upload new/modified local expenses
            uploadLocalExpenses(userId)

            // 2. Download remote expenses
//            downloadRemoteExpenses(userId)

            // 3. Sync categories (read-only for employees)
//            syncCategories(userId)

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

            // 3. Sync categories (read-only for employees)

        } catch (e: Exception) {
            Log.e("Sync", "Error syncing data", e)
        }
    }

    suspend fun syncPersons() {
        // Sync persons data
    }

    suspend fun syncCategories() {
        /*// Categories are read-only for employees, but they need local copies
        val globalCategories = firestore.collection("globalCategories")
            .get()
            .await()

        globalCategories.documents.forEach { doc ->
            val category = CategoryEntity()
            val existingCategory = categoryDao
                .getCategoryByFirestoreId(doc.id)

            if (existingCategory == null) {
                categoryDao.insert(category)
            } else {
                // Update if needed
                categoryDao.update(category.copy(categoryId = existingCategory.categoryId))
            }
        }*/
    }

    private suspend fun uploadLocalExpenses(userId: String) {
        val unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId)
        val userExpensesRef = firestore.collection("users")
            .document(userId)
            .collection("expenses")

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

            // Upload to Firestore
            userExpensesRef.document(firestoreDocId)
                .set(expenseData)
                .await()

            // Update local record with Firestore ID
            expenseDao.updateSyncStatus(
                expenseId = expense.expenseId,
                firestoreId = firestoreDocId,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

    private suspend fun uploadLocalIncomes(userId: String) {
        val unsyncedIncomes = incomeDao.getUnsyncedIncomes(userId)
        val userIncomesRef = firestore.collection("users")
            .document(userId)
            .collection("incomes")

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

            // Upload to Firestore
            userIncomesRef.document(firestoreDocId)
                .set(incomeData)
                .await()

            // Update local record with Firestore ID
            incomeDao.updateSyncStatus(
                incomeId = income.incomeId,
                firestoreId = firestoreDocId,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

    private suspend fun downloadRemoteExpenses(userId: String) {
        /*val lastSyncTime = getLastSyncTimestamp()

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        snapshot.documents.forEach { doc ->
            val remoteExpense = doc.toExpenseEntity(userId)

            // Check if expense already exists locally
            val existingExpense = expenseDao
                .getExpenseByLocalId(remoteExpense.localId)

            if (existingExpense != null) {
                // Update existing expense
                val resolvedExpense = resolveConflict(existingExpense, remoteExpense)
                expenseDao.update(resolvedExpense)
            } else {
                // Insert new expense
                expenseDao.insert(remoteExpense)
            }
        }*/
    }
}