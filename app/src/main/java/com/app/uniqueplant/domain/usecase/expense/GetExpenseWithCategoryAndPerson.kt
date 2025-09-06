package com.app.uniqueplant.domain.usecase.expense

import com.app.uniqueplant.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetExpenseWithCategoryAndPerson @Inject constructor(
    private val expenseRepository: ExpenseRepository
){
    suspend operator fun invoke(id: Long) = expenseRepository.getExpensesWithCategoryAndPerson(id)
}