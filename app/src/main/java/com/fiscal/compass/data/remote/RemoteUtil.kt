package com.fiscal.compass.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object RemoteUtil {

    // Firestore Collection Paths
    object Paths {
        const val EXPENSES = "expenses"
        const val INCOMES = "incomes"
        const val CATEGORIES = "categories"
        const val PERSONS = "persons"
        const val USERS = "users"
        const val SYNC_METADATA = "sync_metadata"
    }

    // Document ID Generation Methods

    /**
     * Generates a new document ID for expenses
     */
    fun generateExpenseId(): String {
        return FirebaseFirestore.getInstance()
            .collection(Paths.EXPENSES)
            .document()
            .id
    }

    /**
     * Generates a new document ID for incomes
     */
    fun generateIncomeId(): String {
        return FirebaseFirestore.getInstance()
            .collection(Paths.INCOMES)
            .document()
            .id
    }

    /**
     * Generates a new document ID for categories
     */
    fun generateCategoryId(): String {
        return FirebaseFirestore.getInstance()
            .collection(Paths.CATEGORIES)
            .document()
            .id
    }

    /**
     * Generates a new document ID for persons
     */
    fun generatePersonId(): String {
        return FirebaseFirestore.getInstance()
            .collection(Paths.PERSONS)
            .document()
            .id
    }

    /**
     * Generates a UUID-based ID (alternative method)
     */
    fun generateUUIDBasedId(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Generates a timestamped ID with prefix
     */
    fun generateTimestampedId(prefix: String): String {
        return "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
    }

    // Collection Reference Helper Methods

    /**
     * Gets the full path for an expense document
     */
    fun getExpensePath(expenseId: String): String {
        return "${Paths.EXPENSES}/$expenseId"
    }

    /**
     * Gets the full path for an income document
     */
    fun getIncomePath(incomeId: String): String {
        return "${Paths.INCOMES}/$incomeId"
    }

    /**
     * Gets the full path for a category document
     */
    fun getCategoryPath(categoryId: String): String {
        return "${Paths.CATEGORIES}/$categoryId"
    }

    /**
     * Gets the full path for a person document
     */
    fun getPersonPath(personId: String): String {
        return "${Paths.PERSONS}/$personId"
    }

    /**
     * Gets the full path for a user document
     */
    fun getUserPath(userId: String): String {
        return "${Paths.USERS}/$userId"
    }

    // Validation Methods

    /**
     * Validates if a document ID is valid (non-empty and non-blank)
     */
    fun isValidDocumentId(id: String?): Boolean {
        return !id.isNullOrBlank()
    }

    /**
     * Ensures a document ID is valid, generates a new one if not
     */
    fun ensureValidExpenseId(id: String?): String {
        return if (isValidDocumentId(id)) id!! else generateExpenseId()
    }

    /**
     * Ensures a document ID is valid, generates a new one if not
     */
    fun ensureValidIncomeId(id: String?): String {
        return if (isValidDocumentId(id)) id!! else generateIncomeId()
    }

    /**
     * Ensures a document ID is valid, generates a new one if not
     */
    fun ensureValidCategoryId(id: String?): String {
        return if (isValidDocumentId(id)) id!! else generateCategoryId()
    }

    /**
     * Ensures a document ID is valid, generates a new one if not
     */
    fun ensureValidPersonId(id: String?): String {
        return if (isValidDocumentId(id)) id!! else generatePersonId()
    }
}