package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetExpenseWithCategoryAndPerson @Inject constructor(
    private val expenseRepository: ExpenseRepository
){
    suspend operator fun invoke(id: Long) = expenseRepository.getSingleFulExpenseById(id)
}