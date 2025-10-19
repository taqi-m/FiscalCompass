package com.app.uniqueplant.domain.usecase.income

import com.app.uniqueplant.domain.model.base.Income
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserIncomesUC @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    suspend fun invoke(userId: String): Flow<List<Income>> {
        return incomeRepository.getIncomesByUser(userId)
    }
}