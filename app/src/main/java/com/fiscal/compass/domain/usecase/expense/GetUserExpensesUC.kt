package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserExpensesUC @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend fun invoke(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId)
    }
}
