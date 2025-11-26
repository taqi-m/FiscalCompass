package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.IncomeUi
import com.fiscal.compass.presentation.model.IncomeWithCategoryAndPersonUi
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Income.toUi(): IncomeUi {
    return IncomeUi(
        incomeId = incomeId,
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        formatedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date),
        formatedTime = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date),
        description = description
    )
}

fun IncomeFull.toUi(): IncomeWithCategoryAndPersonUi {
    return IncomeWithCategoryAndPersonUi(
        income = income.toUi(),
        category = category?.toUi(),
        person = person?.toUi()
    )
}