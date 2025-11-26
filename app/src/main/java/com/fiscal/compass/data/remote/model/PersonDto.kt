package com.fiscal.compass.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class PersonDto(
    @get:PropertyName("firestoreId") @set:PropertyName("firestoreId")
    var firestoreId: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("personType") @set:PropertyName("personType")
    var personType: String = "",

    @get:PropertyName("contact") @set:PropertyName("contact")
    var contact: String = "",

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp = Timestamp.now(),

    @get:PropertyName("isSynced") @set:PropertyName("isSynced")
    var isSynced: Boolean = false,

    @get:PropertyName("needsSync") @set:PropertyName("needsSync")
    var needsSync: Boolean = false,

    @get:PropertyName("lastSyncedAt") @set:PropertyName("lastSyncedAt")
    var lastSyncedAt: Timestamp? = null

)
