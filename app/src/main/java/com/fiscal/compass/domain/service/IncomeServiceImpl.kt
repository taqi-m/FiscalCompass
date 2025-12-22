package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.model.IncomeWithCategory
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.validation.PaymentValidation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class IncomeServiceImpl @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository
) : IncomeService {

    override suspend fun addIncome(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        amountPaid: Double
    ): Result<Unit> {
        return try {
            val uid: String? = sessionUseCase.getCurrentUser()?.uid
            if (uid.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("User is not logged in"))
            }

            PaymentValidation.validatePaymentAmount(amount, amountPaid).getOrElse {
                return Result.failure(it)
            }

            val newIncome = Income(
                amount = amount,
                amountPaid = amountPaid,
                description = description,
                date = date,
                categoryId = categoryId,
                userId = uid
            )

            incomeRepository.addIncome(newIncome)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId)
    }

    override suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>> {
        return incomeRepository.getIncomesWithCategory(userId)
    }

    override suspend fun getSingleFullIncomeById(id: Long): IncomeFull {
        val income = incomeRepository.getSingleFullIncomeById(id)
            ?: throw IllegalArgumentException("Income not found")
        return income
    }

    override suspend fun updateIncomePayment(incomeId: Long, newAmountPaid: Double): Result<Unit> {
        return try {
            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            PaymentValidation.validatePaymentAmount(income.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            val updatedIncome = income.copy(
                amountPaid = newAmountPaid,
                updatedAt = Date()
            )

            incomeRepository.updateIncome(updatedIncome)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPayment(incomeId: Long, paymentAmount: Double): Result<Unit> {
        return try {
            if (paymentAmount <= 0) {
                return Result.failure(IllegalArgumentException("Payment amount must be greater than zero"))
            }

            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            val newAmountPaid = income.amountPaid + paymentAmount

            PaymentValidation.validatePaymentAmount(income.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            updateIncomePayment(incomeId, newAmountPaid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsFullyReceived(incomeId: Long): Result<Unit> {
        return try {
            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            updateIncomePayment(incomeId, income.amount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFullyReceivedIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { PaymentValidation.isFullyReceived(it) }
        }
    }

    override suspend fun getPartiallyReceivedIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { PaymentValidation.isPartiallyReceived(it) }
        }
    }

    override suspend fun getUnpaidIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { it.amountPaid == 0.0 }
        }
    }

    override suspend fun getPendingIncomes(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.filter { !PaymentValidation.isFullyReceived(it) }
        }
    }

    override suspend fun getTotalOutstandingIncome(userId: String): Flow<Double> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.sumOf { PaymentValidation.getOutstandingIncome(it) }
        }
    }

    override suspend fun getTotalReceivedAmount(userId: String): Flow<Double> {
        return incomeRepository.getIncomesByUser(userId).map { incomes ->
            incomes.sumOf { it.amountPaid }
        }
    }
}
