package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.ExpenseEntity
import com.fiscal.compass.data.remote.model.ExpenseDto
import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.Transaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

/**
 * Mapper functions to convert between [ExpenseEntity] and [Expense].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        expenseId = this.expenseId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        description = this.description,
        date = Date(this.date),
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

fun Expense.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.expenseId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        categoryId = this.categoryId,
        personId = this.personId,
        date = this.date,
        description = this.description,
        isExpense = true,
        transactionType = "Expense"
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.expenseId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        description = this.description,
        date = this.date.time,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
        // Default sync tracking fields for new expenses
        isDeleted = false,
        isSynced = false,
        needsSync = true,
        lastSyncedAt = null
    )
}

/**
 * Converts an [ExpenseEntity] to [ExpenseDto] for Firestore operations.
 */
fun ExpenseEntity.toDto(): ExpenseDto {
    return ExpenseDto(
        id = expenseId,
        amount = amount,
        amountPaid = amountPaid,
        userId = userId,
        categoryId = categoryId,
        personId = personId,
        description = description,
        date = Timestamp(date / 1000, ((date % 1000) * 1_000_000).toInt()),
        paymentMethod = paymentMethod,
        location = location,
        receipt = receipt,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt()),
        isDeleted = isDeleted,
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        }
    )
}

/**
 * Converts an [ExpenseDto] to [ExpenseEntity].
 */
fun ExpenseDto.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = id, // id is already a String
        amount = amount,
        amountPaid = amountPaid,
        categoryId = categoryId,
        userId = userId,
        personId = personId,
        description = description,
        date = date.toDate().time,
        paymentMethod = paymentMethod,
        location = location,
        receipt = receipt,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isDeleted = isDeleted,
        isSynced = true,
        needsSync = false,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}

/**
 * Updates an [ExpenseDto] with sync timestamps for Firestore upload.
 */
fun ExpenseDto.withSyncTimestamp(syncTime: Long = System.currentTimeMillis()): ExpenseDto {
    val timestamp = Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    return this.copy(
        updatedAt = timestamp,
        lastSyncedAt = timestamp
    )
}

/**
 * Converts a Firestore [DocumentSnapshot] to [ExpenseDto].
 */
fun DocumentSnapshot.toExpenseDto(): ExpenseDto? {
    if (!exists()) return null

    return ExpenseDto(
        id = id, // Document ID as String
        amount = getDouble("amount") ?: 0.0,
        amountPaid = getDouble("amountPaid") ?: 0.0,
        userId = getString("userId") ?: "",
        categoryId = getString("categoryId") ?: "",
        personId = getString("personId"),
        description = getString("description") ?: "",
        date = getTimestamp("date") ?: Timestamp.now(),
        paymentMethod = getString("paymentMethod"),
        location = getString("location"),
        receipt = getString("receipt"),
        isRecurring = getBoolean("isRecurring") ?: false,
        recurringFrequency = getString("recurringFrequency"),
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now(),
        isDeleted = getBoolean("isDeleted") ?: false,
        lastSyncedAt = getTimestamp("lastSyncedAt")
    )
}
