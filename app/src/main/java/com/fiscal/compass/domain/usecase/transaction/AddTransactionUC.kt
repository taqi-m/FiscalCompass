package com.fiscal.compass.domain.usecase.transaction

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.usecase.categories.GetCategoriesUseCase
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.model.TransactionType
import javax.inject.Inject

class AddTransactionUC @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase
) {
    suspend operator fun invoke(tr: Transaction, amountPaid: Double = 0.0): Result<Long> {
        return try {
            if (tr.amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            // Validate payment amount
            PaymentValidation.validatePaymentAmount(tr.amount, amountPaid).getOrElse {
                return Result.failure(it)
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
                    amountPaid = amountPaid,
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
                    amountPaid = amountPaid,
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