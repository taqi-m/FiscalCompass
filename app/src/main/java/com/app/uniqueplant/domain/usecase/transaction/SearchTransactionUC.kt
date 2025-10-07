package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mappers.toTransaction
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.exp

class SearchTransactionUC @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(
        personIds: List<Long>?,   // pass null to ignore
        categoryIds: List<Long>?, // pass null to ignore
        startDate: Long?,         // nullable → open start
        endDate: Long?,            // nullable → open end
        filterType: String? = null // "income", "expense", or null for both
    ): Map<Date, List<Transaction>> {
        val expenses = if (filterType?.lowercase() == "income") {
            emptyList()
        } else {
            expenseRepository.getAllFiltered(
                personIds =  personIds,
                categoryIds =  categoryIds,
                startDate =  startDate,
                endDate =  endDate
            ).first()
        }
        val incomes = if (filterType?.lowercase() == "expense") {
            emptyList()
        } else {
            incomeRepository.getAllFiltered(
                personIds =  personIds,
                categoryIds =  categoryIds,
                startDate =  startDate,
                endDate =  endDate
            ).first()
        }

        return mergeAndGroupTransactions(expenses, incomes)
    }

    private fun mergeAndGroupTransactions(expenses: List<Expense>, incomes: List<Income>): Map<Date, List<Transaction>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expenseTransactions = expenses.map { expense ->
            expense.toTransaction()
        }
        val incomeTransactions = incomes.map { income ->
            income.toTransaction()
        }
        return (expenseTransactions + incomeTransactions)
            .sortedByDescending { it.date }
            .groupBy { transaction ->
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                dateFormatter.parse(dateFormatter.format(transaction.date))
            }
    }


}