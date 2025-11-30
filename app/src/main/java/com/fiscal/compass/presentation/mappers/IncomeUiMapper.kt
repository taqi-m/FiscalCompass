package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.IncomeUi
import com.fiscal.compass.presentation.model.IncomeWithCategoryAndPersonUi
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Income.toUi(): IncomeUi {
    val remainingAmount = amount - amountPaid
    val progress = PaymentValidation.getPaymentProgress(amount, amountPaid)
    val isFullyReceived = PaymentValidation.isFullyReceived(this)

    return IncomeUi(
        incomeId = incomeId,
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        formatedAmountPaid = CurrencyFormater.formatCurrency(amountPaid),
        formatedRemainingAmount = CurrencyFormater.formatCurrency(remainingAmount),
        formatedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date),
        formatedTime = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date),
        description = description,
        isFullyPaid = isFullyReceived,
        paymentProgressPercentage = progress.toInt()
    )
}

fun IncomeFull.toUi(): IncomeWithCategoryAndPersonUi {
    return IncomeWithCategoryAndPersonUi(
        income = income.toUi(),
        category = category?.toUi(),
        person = person?.toUi()
    )
}

