package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.IncomeUi
import com.fiscal.compass.presentation.model.IncomeWithCategoryAndPersonUi

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Income.toUi(): IncomeUi {
    val remainingAmount = amount - amountPaid
    val progress = PaymentValidation.getPaymentProgress(amount, amountPaid)
    val isFullyReceived = PaymentValidation.isFullyReceived(this)

    return IncomeUi(
        incomeId = incomeId, // Now String type
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        formatedAmountPaid = CurrencyFormater.formatCurrency(amountPaid),
        formatedRemainingAmount = CurrencyFormater.formatCurrency(remainingAmount),
        formatedDate = DateTimeUtil.formatDate(date, DATE_FORMAT),
        formatedTime = DateTimeUtil.formatTime(date, TIME_FORMAT),
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

