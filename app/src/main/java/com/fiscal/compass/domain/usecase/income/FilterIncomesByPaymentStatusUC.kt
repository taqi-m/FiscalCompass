package com.fiscal.compass.domain.usecase.income

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.validation.PaymentValidation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for filtering incomes by payment status
 */
class FilterIncomesByPaymentStatusUC @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    /**
     * Get all fully received incomes for a user
     */
    suspend fun getFullyReceivedIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { PaymentValidation.isFullyReceived(it) }
        }
    }

    /**
     * Get all partially received incomes for a user
     */
    suspend fun getPartiallyReceivedIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { PaymentValidation.isPartiallyReceived(it) }
        }
    }

    /**
     * Get all unpaid incomes (amountPaid = 0) for a user
     */
    suspend fun getUnpaidIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { it.amountPaid == 0.0 }
        }
    }

    /**
     * Get all pending incomes (not fully received) for a user
     */
    suspend fun getPendingIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { !PaymentValidation.isFullyReceived(it) }
        }
    }

    /**
     * Calculate total outstanding income amount for a user
     */
    suspend fun getTotalOutstandingIncome(userId: String): Flow<Double> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.sumOf { PaymentValidation.getOutstandingIncome(it) }
        }
    }

    /**
     * Calculate total received amount for a user
     */
    suspend fun getTotalReceivedAmount(userId: String): Flow<Double> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.sumOf { it.amountPaid }
        }
    }
}

