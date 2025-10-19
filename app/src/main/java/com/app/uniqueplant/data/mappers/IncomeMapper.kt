package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.data.remote.model.IncomeDto
import com.app.uniqueplant.domain.model.base.Income
import com.app.uniqueplant.domain.model.Transaction
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
        source = source,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        isTaxable = isTaxable,
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
 * Converts an [IncomeDto] to [IncomeEntity].
 */
fun IncomeDto.toEntity(): IncomeEntity {
    return IncomeEntity(
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
        source = source,
        isRecurring = isRecurring,
        recurringFrequency = recurringFrequency,
        isTaxable = isTaxable,
        createdAt = createdAt.toDate().time,
        updatedAt = updatedAt.toDate().time,
        isDeleted = isDeleted,
        isSynced = isSynced,
        needsSync = needsSync,
        lastSyncedAt = lastSyncedAt?.toDate()?.time
    )
}

/**
 * Converts an [IncomeDto] to a Firestore map.
 */
fun IncomeDto.toFirestoreMap(firestoreId: String, syncTime: Long): Map<String, Any?> {
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
        "source" to source,
        "isRecurring" to isRecurring,
        "recurringFrequency" to recurringFrequency,
        "isTaxable" to isTaxable,
        "createdAt" to createdAt,
        "updatedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt()),
        "isDeleted" to isDeleted,
        "isSynced" to true,
        "needsSync" to false,
        "lastSyncedAt" to Timestamp(syncTime / 1000, ((syncTime % 1000) * 1_000_000).toInt())
    )
}

/**
 * Converts a Firestore [DocumentSnapshot] to [IncomeDto].
 */
fun DocumentSnapshot.toIncomeDto(): IncomeDto? {
    if (!exists()) return null

    return IncomeDto(
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
        source = getString("source"),
        isRecurring = getBoolean("isRecurring") ?: false,
        recurringFrequency = getString("recurringFrequency"),
        isTaxable = getBoolean("isTaxable") ?: true,
        createdAt = getTimestamp("createdAt") ?: Timestamp.now(),
        updatedAt = getTimestamp("updatedAt") ?: Timestamp.now(),
        isDeleted = getBoolean("isDeleted") ?: false,
        isSynced = getBoolean("isSynced") ?: false,
        needsSync = getBoolean("needsSync") ?: true,
        lastSyncedAt = getTimestamp("lastSyncedAt")
    )
}
