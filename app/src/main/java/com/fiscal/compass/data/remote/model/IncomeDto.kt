package com.fiscal.compass.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class IncomeDto(
    @get:PropertyName("id") @set:PropertyName("incomeId")
    var id: String = "",

    @get:PropertyName("userId") @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("categoryId") @set:PropertyName("categoryId")
    var categoryId: String = "",

    @get:PropertyName("personId") @set:PropertyName("personId")
    var personId: String? = null,

    @get:PropertyName("amount") @set:PropertyName("amount")
    var amount: Double = 0.0,

    @get:PropertyName("amountPaid") @set:PropertyName("amountPaid")
    var amountPaid: Double = 0.0,

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Timestamp = Timestamp.now(),

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

    @get:PropertyName("lastSyncedAt") @set:PropertyName("lastSyncedAt")
    var lastSyncedAt: Timestamp? = null
)
