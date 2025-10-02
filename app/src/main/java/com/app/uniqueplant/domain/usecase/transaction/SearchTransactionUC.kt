package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mappers.toTransaction
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val incomeTransactions = if (filterType?.lowercase() == "expense") {
            emptyList()
        } else {
            incomeRepository.getAllFullIncomesFiltered(
                personIds,
                categoryIds,
                startDate,
                endDate
            ).map {
                it.toTransaction()
            }
        }
        val expenseTransactions = if (filterType?.lowercase() == "income") {
            emptyList()
        } else {
            expenseRepository.getAllFullExpensesFiltered(
                personIds,
                categoryIds,
                startDate,
                endDate
            ).map {
                it.toTransaction()
            }
        }
        val transactions = incomeTransactions + expenseTransactions
        val groupedTransactions = transactions.sortedByDescending { it.date }
            .groupBy { transaction ->
                dateFormatter.parse(dateFormatter.format(transaction.date))
            }
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return groupedTransactions
    }
}