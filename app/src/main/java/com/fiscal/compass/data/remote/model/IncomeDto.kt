package com.fiscal.compass.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class IncomeDto(
    @get:PropertyName("firestoreId") @set:PropertyName("firestoreId")
    var firestoreId: String = "",

    @get:PropertyName("localId") @set:PropertyName("localId")
    var localId: String = "",

    @get:PropertyName("amount") @set:PropertyName("amount")
    var amount: Double = 0.0,

    @get:PropertyName("amountPaid") @set:PropertyName("amountPaid")
    var amountPaid: Double = 0.0,

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Timestamp = Timestamp.now(),

    @get:PropertyName("categoryId") @set:PropertyName("categoryId")
    var categoryId: Long = 0,

    @get:PropertyName("categoryFirestoreId") @set:PropertyName("categoryFirestoreId")
    var categoryFirestoreId: String = "",

    @get:PropertyName("userId") @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("personId") @set:PropertyName("personId")
    var personId: Long? = null,

    @get:PropertyName("personFirestoreId") @set:PropertyName("personFirestoreId")
    var personFirestoreId: String? = null,

    @get:PropertyName("source") @set:PropertyName("source")
    var source: String? = null,

    @get:PropertyName("isRecurring") @set:PropertyName("isRecurring")
    var isRecurring: Boolean = false,

    @get:PropertyName("recurringFrequency") @set:PropertyName("recurringFrequency")
    var recurringFrequency: String? = null,

    @get:PropertyName("isTaxable") @set:PropertyName("isTaxable")
    var isTaxable: Boolean = true,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp = Timestamp.now(),

    @get:PropertyName("isDeleted") @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    @get:PropertyName("isSynced") @set:PropertyName("isSynced")
    var isSynced: Boolean = false,

    @get:PropertyName("needsSync") @set:PropertyName("needsSync")
    var needsSync: Boolean = true,

    @get:PropertyName("lastSyncedAt") @set:PropertyName("lastSyncedAt")
    var lastSyncedAt: Timestamp? = null
)
