package com.app.uniqueplant.presentation.transactions

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.domain.model.TransactionType
import java.util.Date

data class AddTransactionState(
    val amount: InputField = InputField(
        value = "0.0"
    ),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val description: InputField = InputField(),
    val date: Date,
    val categoryId: Long,
    val accountId: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String = "",
    val isDateDialogOpen: Boolean = false,
    val isTimeDialogOpen: Boolean = false,
    val categories: List<Category> = emptyList()
)