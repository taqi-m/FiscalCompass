package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.data.remote.model.IncomeDto
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.Transaction
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date


/**
 * Mapper functions to convert between [IncomeEntity] and [Income].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun IncomeEntity.toDomain(): Income {
    return Income(
        incomeId = this.incomeId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        description = this.description,
        date = Date(this.date),
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

fun Income.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.incomeId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        categoryId = this.categoryId,
        personId = this.personId,
        date = this.date,
        description = this.description,
        isExpense = false,
        transactionType = "Income"
    )
}

fun Income.toIncomeEntity(): IncomeEntity {
    return IncomeEntity(
        incomeId = this.incomeId,
        amount = this.amount,
        amountPaid = this.amountPaid,
        description = this.description,
        date = this.date.time,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time
    )
}

/**
 * Converts an [IncomeEntity] to [IncomeDto] for Firestore operations.
 */
fun IncomeEntity.toDto(): IncomeDto {
    return IncomeDto(
        id = incomeId,
        userId = userId,
        categoryId = categoryId,
        amount = amount,
        amountPaid = amountPaid,
        description = description,
        date = Timestamp(date / 1000, ((date % 1000) * 1_000_000).toInt()),
        source = source,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        isTaxable = isTaxable,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1_000_000).toInt()),
        isDeleted = isDeleted,
        lastSyncedAt = lastSyncedAt?.let {
            Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
        }
    )
}

/**
 * Converts an [IncomeDto] to [IncomeEntity].
 */
fun IncomeDto.toEntity(): IncomeEntity {
    return IncomeEntity(
        incomeId = id,
        userId = userId,
        categoryId = categoryId,
        personId = personId,
        amount = amount,
        amountPaid = amountPaid,
        description = description,
        date = date.toDate().time,
        source = source,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        isTaxable = isTaxable,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isDeleted = isDeleted,
        isSynced = true,
        needsSync = false,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}

/**
 * Updates an [IncomeDto] with sync timestamps for Firestore upload.
 */
fun IncomeDto.withSyncTimestamp(syncTime: Long = System.currentTimeMillis()): IncomeDto {
    val timestamp = Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    return this.copy(
        updatedAt = timestamp,
        lastSyncedAt = timestamp
    )
}

/**
 * Converts an [IncomeEntity] into a stable key/value map for Firestore writes.
 * This avoids reflection-based serialization that can break under obfuscation.
 */
fun IncomeEntity.toFirestoreMap(
    syncTime: Long = System.currentTimeMillis(),
    forceDeleted: Boolean = isDeleted
): Map<String, Any?> {
    val timestamp = Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    return mapOf(
        "id" to incomeId,
        "userId" to userId,
        "categoryId" to categoryId,
        "personId" to personId,
        "amount" to amount,
        "amountPaid" to amountPaid,
        "description" to description,
        "date" to Timestamp(date / 1000, ((date % 1000) * 1_000_000).toInt()),
        "source" to source,
        "isRecurring" to isRecurring,
        "recurringFrequency" to recurringFrequency,
        "isTaxable" to isTaxable,
        "createdAt" to Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        "updatedAt" to timestamp,
        "isDeleted" to forceDeleted,
        "lastSyncedAt" to timestamp
    )
}

/**
 * Converts a Firestore [DocumentSnapshot] to [IncomeDto].
 */
fun DocumentSnapshot.toIncomeDto(): IncomeDto? {
    if (!exists()) return null

    return IncomeDto(
        id = id, // Document ID as String
        amount = getDouble("amount") ?: 0.0,
        amountPaid = getDouble("amountPaid") ?: 0.0,
        userId = getString("userId") ?: "",
        categoryId = getString("categoryId") ?: "",
        personId = getString("personId"),
        description = getString("description") ?: "",
        date = getTimestamp("date") ?: Timestamp.now(),
        source = getString("source"),
        isRecurring = getBoolean("isRecurring") ?: false,
        recurringFrequency = getString("recurringFrequency"),
        isTaxable = getBoolean("isTaxable") ?: true,
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now(),
        isDeleted = getBoolean("isDeleted") ?: false,
        lastSyncedAt = getTimestamp("lastSyncedAt")
    )
}
