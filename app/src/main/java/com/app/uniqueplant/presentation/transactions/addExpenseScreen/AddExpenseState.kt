package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.InputField
import java.util.Date

data class AddExpenseState(
    val amount: InputField = InputField(
        value = "0.0"
    ),
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