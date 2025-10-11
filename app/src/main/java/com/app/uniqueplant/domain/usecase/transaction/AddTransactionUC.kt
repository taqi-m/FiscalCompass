package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.domain.model.dataModels.Expense
import com.app.uniqueplant.domain.model.dataModels.Income
import com.app.uniqueplant.domain.model.dataModels.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import com.app.uniqueplant.domain.usecase.categories.GetCategoriesUseCase
import com.app.uniqueplant.presentation.model.TransactionType
import javax.inject.Inject

class AddTransactionUC @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase
) {
    suspend operator fun invoke(tr: Transaction): Result<Long> {
        return try {
            if (tr.amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            val isExpense = tr.isExpense

            val category = getCategoriesUseCase.getCategoryById(tr.categoryId)
                ?: return Result.failure(IllegalArgumentException("Invalid categoryId ID"))

            if(category.isExpenseCategory != isExpense) {
                return Result.failure(IllegalArgumentException("Category type does not match transaction type"))
            }


            if (tr.transactionType == TransactionType.INCOME.name) {
                val newIncome = Income(
                    amount = tr.amount,
                    description = tr.description ?: "",
                    date = tr.date,
                    categoryId = tr.categoryId,
                    personId = tr.personId,
                    userId = uid
                )
                Result.success(incomeRepository.addIncome(newIncome))
            } else {
                val newExpense = Expense(
                    amount = tr.amount,
                    description = tr.description ?: "",
                    date = tr.date,
                    categoryId = tr.categoryId,
                    personId = tr.personId,
                    userId = uid
                )
                Result.success(expenseRepository.addExpense(newExpense))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}