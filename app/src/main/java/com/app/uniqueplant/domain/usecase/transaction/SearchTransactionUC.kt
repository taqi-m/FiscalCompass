package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mappers.toTransaction
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.model.base.Expense
import com.app.uniqueplant.domain.model.base.Income
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
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

        val adjustedStartDate = startDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        val adjustedEndDate = endDate?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            calendar.timeInMillis
        }
        val expenses = if (filterType?.lowercase() == "income") {
            emptyList()
        } else {
            expenseRepository.getAllFiltered(
                personIds =  personIds,
                categoryIds =  categoryIds,
                startDate =  adjustedStartDate,
                endDate =  adjustedEndDate
            ).first()
        }
        val incomes = if (filterType?.lowercase() == "expense") {
            emptyList()
        } else {
            incomeRepository.getAllFiltered(
                personIds =  personIds,
                categoryIds =  categoryIds,
                startDate =  adjustedStartDate,
                endDate =  adjustedEndDate
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