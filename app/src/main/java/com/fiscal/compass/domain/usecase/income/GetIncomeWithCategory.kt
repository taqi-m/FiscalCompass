package com.fiscal.compass.domain.usecase.income

import com.fiscal.compass.domain.repository.IncomeRepository
import javax.inject.Inject

class GetIncomeWithCategory @Inject constructor(
    private val incomeRepository: IncomeRepository
){
    suspend operator fun invoke(userId: String) = incomeRepository.getIncomesWithCategory(userId)
}