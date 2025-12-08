package com.fiscal.compass.domain.usecase.transaction

import com.fiscal.compass.data.mappers.toTransaction
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.model.TransactionType
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionService @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val categoryRepository: CategoryRepository
) {
    /**
     * Adds a new transaction to the database.
     * @param tr The transaction to be added.
     * @return A [Result] containing the ID of the added transaction or an error message.
     */
    suspend fun addTransaction(tr: Transaction): Result<Long> {
        return try {
            val amount = tr.amount
            val amountPaid = tr.amountPaid
            if (amount <= 0) {
                return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
            }

            // Validate payment amount
            PaymentValidation.validatePaymentAmount(amount, amountPaid).getOrElse {
                return Result.failure(it)
            }

            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            val isExpense = tr.isExpense

            val category = categoryRepository.getCategoryById(tr.categoryId)
                ?: return Result.failure(IllegalArgumentException("Invalid categoryId ID"))

            if (category.isExpenseCategory != isExpense) {
                return Result.failure(IllegalArgumentException("Category type does not match transaction type"))
            }


            if (tr.transactionType == TransactionType.INCOME.name) {
                val newIncome = Income(
                    amount = amount,
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
                    amount = amount,
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

    /**
     * Searches for transactions based on various filter criteria.
     *
     * @param personIds Optional list of person IDs to filter by. Pass null to ignore.
     * @param categoryIds Optional list of category IDs to filter by. Pass null to ignore.
     * @param dateRange Optional date range for filtering transactions. Pass null for no date filtering.
     * @param filterType Filter by transaction type: "income", "expense", or null for both.
     * @return Map of transactions grouped by date, sorted in descending order.
     */
    suspend fun searchTransactions(
        personIds: List<Long>? = null,
        categoryIds: List<Long>? = null,
        dateRange: DateRange? = null,
        filterType: String? = null
    ): Map<Date, List<Transaction>> {
        val expenses = if (filterType?.lowercase() == "income") {
            emptyList()
        } else {
            expenseRepository.getAllFiltered(
                personIds = personIds,
                categoryIds = categoryIds,
                startDate = dateRange?.startDate,
                endDate = dateRange?.endDate
            ).first()
        }
        val incomes = if (filterType?.lowercase() == "expense") {
            emptyList()
        } else {
            incomeRepository.getAllFiltered(
                personIds = personIds,
                categoryIds = categoryIds,
                startDate = dateRange?.startDate,
                endDate = dateRange?.endDate
            ).first()
        }

        return mergeAndGroupTransactions(expenses, incomes)
    }

    /**
     * Searches for transactions based on various filter criteria with explicit start and end dates.
     * This is a convenience method that creates a DateRange internally.
     *
     * @param personIds Optional list of person IDs to filter by. Pass null to ignore.
     * @param categoryIds Optional list of category IDs to filter by. Pass null to ignore.
     * @param startDate Optional start timestamp in milliseconds. Pass null for no lower bound.
     * @param endDate Optional end timestamp in milliseconds. Pass null for no upper bound.
     * @param filterType Filter by transaction type: "income", "expense", or null for both.
     * @return Map of transactions grouped by date, sorted in descending order.
     */
    suspend fun searchTransactions(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?,
        filterType: String? = null
    ): Map<Date, List<Transaction>> {
        val dateRange = if (startDate != null || endDate != null) {
            DateRange.from(startDate, endDate)
        } else {
            null
        }
        return searchTransactions(personIds, categoryIds, dateRange, filterType)
    }

    /**
     * Merges expense and income lists into transactions and groups them by date.
     *
     * @param expenses List of expenses to include
     * @param incomes List of incomes to include
     * @return Map of transactions grouped by date, sorted in descending order
     */
    private fun mergeAndGroupTransactions(
        expenses: List<Expense>,
        incomes: List<Income>
    ): Map<Date, List<Transaction>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expenseTransactions = expenses.map { it.toTransaction() }
        val incomeTransactions = incomes.map { it.toTransaction() }

        return (expenseTransactions + incomeTransactions)
            .sortedByDescending { it.date }
            .groupBy { transaction ->
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                dateFormatter.parse(dateFormatter.format(transaction.date))
            }
    }
}