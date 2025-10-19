package com.app.uniqueplant.domain.usecase.expense

import com.app.uniqueplant.domain.model.base.Expense
import com.app.uniqueplant.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserExpensesUC @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend fun invoke(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId)
    }
}
