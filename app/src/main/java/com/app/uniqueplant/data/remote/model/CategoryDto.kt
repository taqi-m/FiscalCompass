package com.app.uniqueplant.data.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class CategoryDto(
    @get:PropertyName("firestoreId") @set:PropertyName("firestoreId")
    var firestoreId: String = "",

    @get:PropertyName("color") @set:PropertyName("color")
    var color: Int = 0,

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("expectedPersonType") @set:PropertyName("expectedPersonType")
    var expectedPersonType: String = "",

    @get:PropertyName("icon") @set:PropertyName("icon")
    var icon: String = "",

    @get:PropertyName("isExpenseCategory") @set:PropertyName("isExpenseCategory")
    var isExpenseCategory: Boolean = false,

    @get:PropertyName("isSynced") @set:PropertyName("isSynced")
    var isSynced: Boolean = false,

    @get:PropertyName("lastSyncedAt") @set:PropertyName("lastSyncedAt")
    var lastSyncedAt: Timestamp? = null,

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("needsSync") @set:PropertyName("needsSync")
    var needsSync: Boolean = false,

    @get:PropertyName("parentCategoryFirestoreId") @set:PropertyName("parentCategoryFirestoreId")
    var parentCategoryFirestoreId: String = "",

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Timestamp = Timestamp.now()
)

