package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.presentation.model.IncomeUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Income.toIncomeUi(): IncomeUi {
    return IncomeUi(
        incomeId = incomeId,
        formatedAmount = CurrencyFormaterUseCase.formatCurrency(amount),
        formatedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date),
        formatedTime = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date),
        description = description
    )
}