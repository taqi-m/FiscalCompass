package com.app.uniqueplant.presentation.transactions.addIncomeScreen

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.InputField
import java.util.Date

data class AddIncomeState(
    val amount: InputField = InputField(),
    val description: InputField = InputField(),
    val date: Date,
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isDialogOpen: Boolean = false,
    val categories: List<Category> = emptyList()
)

