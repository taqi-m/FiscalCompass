package com.app.uniqueplant.domain.usecase.income

import android.util.Log
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import java.util.Date

class AddIncomeUseCase(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository
) {
    suspend fun addIncome(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date
    ): Result<Unit> {
        return try {
            val uid: String? = sessionUseCase.getCurrentUser()?.uid
            if (uid.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("User is not logged in"))
            }

            val newIncome = Income(
                amount = amount,
                description = description,
                date = date,
                categoryId = categoryId,
                userId = uid
            )

            Log.d(TAG, "Adding income: $newIncome")


            incomeRepository.addIncome(newIncome)
            Result.success(Unit) // Placeholder for successful operation
        } catch (e: Exception) {
            Result.failure(e) // Handle any exceptions that occur
        }
    }

    companion object {
        val TAG = AddIncomeUseCase::class.java.simpleName
    }
}