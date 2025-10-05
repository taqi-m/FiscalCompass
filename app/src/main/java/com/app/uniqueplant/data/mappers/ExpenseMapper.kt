package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.ExpenseEntity
import com.app.uniqueplant.data.remote.model.ExpenseDto
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Transaction
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

fun ExpenseEntity.toRemote(): Map<String, Any?> {
    return mapOf(
        "expenseId" to this.expenseId,
        "amount" to this.amount,
        "description" to this.description,
        "date" to Timestamp(Date(this.date)),
        "categoryId" to this.categoryId,
        "categoryFirestoreId" to this.categoryFirestoreId,
        "userId" to this.userId,
        "personId" to this.personId,
        "personFirestoreId" to this.personFirestoreId,
        "paymentMethod" to this.paymentMethod,
        "location" to this.location,
        "receipt" to this.receipt,
        "isRecurring" to this.isRecurring,
        "recurringFrequency" to this.recurringFrequency,
        "createdAt" to Timestamp(Date(this.createdAt)),
        "updatedAt" to Timestamp(Date(this.updatedAt))
    )
}

fun Expense.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.expenseId,
        amount = this.amount,
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
        updatedAt = this.updatedAt.time
    )
}

/**
 * Converts an [ExpenseEntity] to [ExpenseDto] for Firestore operations.
 */
fun ExpenseEntity.toDto(): ExpenseDto {
    return ExpenseDto(
        firestoreId = firestoreId ?: "",
        localId = localId,
        amount = amount,
        description = description,
        date = Timestamp(date / 1000, ((date % 1000) * 1_000_000).toInt()),
        categoryId = categoryId,
        categoryFirestoreId = categoryFirestoreId ?: "",
        userId = userId,
        personId = personId,
        personFirestoreId = personFirestoreId,
        paymentMethod = paymentMethod,
        location = location,
        receipt = receipt,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt()),
        isDeleted = isDeleted,
        isSynced = isSynced,
        needsSync = needsSync,
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
        firestoreId = firestoreId.ifBlank { null },
        localId = localId,
        amount = amount,
        description = description,
        date = date.toDate().time,
        categoryId = categoryId,
        categoryFirestoreId = categoryFirestoreId.ifBlank { null },
        userId = userId,
        personId = personId,
        personFirestoreId = personFirestoreId?.ifBlank { null },
        paymentMethod = paymentMethod,
        location = location,
        receipt = receipt,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isDeleted = isDeleted,
        isSynced = isSynced,
        needsSync = needsSync,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}

/**
 * Converts an [ExpenseDto] to a Firestore map.
 */
fun ExpenseDto.toFirestoreMap(firestoreId: String, syncTime: Long): Map<String, Any?> {
    return mapOf(
        "firestoreId" to firestoreId,
        "localId" to localId,
        "amount" to amount,
        "description" to description,
        "date" to date,
        "categoryId" to categoryId,
        "categoryFirestoreId" to categoryFirestoreId,
        "userId" to userId,
        "personId" to personId,
        "personFirestoreId" to personFirestoreId,
        "paymentMethod" to paymentMethod,
        "location" to location,
        "receipt" to receipt,
        "isRecurring" to isRecurring,
        "recurringFrequency" to recurringFrequency,
        "createdAt" to createdAt,
        "updatedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt()),
        "isDeleted" to isDeleted,
        "isSynced" to true,
        "needsSync" to false,
        "lastSyncedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    )
}

/**
 * Converts a Firestore [DocumentSnapshot] to [ExpenseDto].
 */
fun DocumentSnapshot.toExpenseDto(): ExpenseDto? {
    if (!exists()) return null

    return ExpenseDto(
        firestoreId = getString("firestoreId") ?: "",
        localId = getString("localId") ?: "",
        amount = getDouble("amount") ?: 0.0,
        description = getString("description") ?: "",
        date = getTimestamp("date") ?: Timestamp.now(),
        categoryId = getLong("categoryId") ?: 0L,
        categoryFirestoreId = getString("categoryFirestoreId") ?: "",
        userId = getString("userId") ?: "",
        personId = getLong("personId"),
        personFirestoreId = getString("personFirestoreId"),
        paymentMethod = getString("paymentMethod"),
        location = getString("location"),
        receipt = getString("receipt"),
        isRecurring = getBoolean("isRecurring") ?: false,
        recurringFrequency = getString("recurringFrequency"),
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now(),
        isDeleted = getBoolean("isDeleted") ?: false,
        isSynced = getBoolean("isSynced") ?: false,
        needsSync = getBoolean("needsSync") ?: true,
        lastSyncedAt = getTimestamp("lastSyncedAt")
    )
}
