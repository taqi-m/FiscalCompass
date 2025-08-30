package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import com.app.uniqueplant.domain.usecase.categories.GetCategoriesUseCase
import com.app.uniqueplant.presentation.model.TransactionType
import java.util.Date
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase
) {
    suspend fun addTransaction(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        transactionType: TransactionType
    ): Result<Long> {
        return try {
            if (amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            val isExpense = transactionType == TransactionType.EXPENSE

            val category = getCategoriesUseCase.getCategoryById(categoryId)
                ?: return Result.failure(IllegalArgumentException("Invalid category ID"))

            if(category.isExpenseCategory != isExpense) {
                return Result.failure(IllegalArgumentException("Category type does not match transaction type"))
            }


            if (transactionType == TransactionType.INCOME) {
                val newIncome = Income(
                    amount = amount,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    userId = uid
                )
                Result.success(incomeRepository.addIncome(newIncome))
            } else {
                val newExpense = Expense(
                    amount = amount,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    userId = uid
                )
                Result.success(expenseRepository.addExpense(newExpense))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}