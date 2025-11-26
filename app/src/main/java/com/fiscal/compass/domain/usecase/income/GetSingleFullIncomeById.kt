package com.fiscal.compass.domain.usecase.income

import com.fiscal.compass.domain.repository.IncomeRepository
import javax.inject.Inject

class GetSingleFullIncomeById @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    suspend operator fun invoke(id: Long) = incomeRepository.getSingleFullIncomeById(id)
}