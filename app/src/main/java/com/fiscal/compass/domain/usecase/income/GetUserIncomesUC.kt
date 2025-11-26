package com.fiscal.compass.domain.usecase.income

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserIncomesUC @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    suspend fun invoke(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId)
    }
}