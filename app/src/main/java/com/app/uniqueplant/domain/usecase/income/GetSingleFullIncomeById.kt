package com.app.uniqueplant.domain.usecase.income

import com.app.uniqueplant.domain.repository.IncomeRepository
import javax.inject.Inject

class GetSingleFullIncomeById @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    suspend operator fun invoke(id: Long) = incomeRepository.getSingleFullIncomeById(id)
}