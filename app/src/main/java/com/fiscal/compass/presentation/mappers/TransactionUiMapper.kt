package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.domain.validation.PaymentValidation
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.TransactionUi
import java.util.Date

private const val DATE_FORMAT = DateTimeUtil.SHORT_DATE_FORMAT
private const val TIME_FORMAT = DateTimeUtil.DEFAULT_TIME_FORMAT

fun formatDate(date: Date): String {
    return DateTimeUtil.formatDate(date, DATE_FORMAT)
}

fun formatTime(date: Date): String {
    return DateTimeUtil.formatTime(date, TIME_FORMAT)
}

fun Transaction.toUi(): TransactionUi {
    val remainingAmount = amount - amountPaid
    val progress = PaymentValidation.getPaymentProgress(amount, amountPaid)
    val isComplete = PaymentValidation.isPaymentComplete(amount, amountPaid)

    return TransactionUi(
        transactionId = transactionId,
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        formatedPaidAmount = CurrencyFormater.formatCurrency(amountPaid),
        formatedRemainingAmount = CurrencyFormater.formatCurrency(remainingAmount),
        categoryId = categoryId,
        personId = personId,
        formatedDate = formatDate(date),
        formatedTime = formatTime(date),
        description = description,
        isExpense = isExpense,
        transactionType = transactionType,
        isFullyPaid = isComplete,
        paymentProgressPercentage = progress.toInt()
    )
}

fun TransactionUi.toTransaction(): Transaction {
    val dateTimeString = "$formatedDate $formatedTime"
    val combinedFormat = "$DATE_FORMAT $TIME_FORMAT"
    val parsedDate = DateTimeUtil.parseDateOrNull(dateTimeString, combinedFormat) ?: Date()

    return Transaction(
        transactionId = transactionId,
        amount = CurrencyFormater.parseCurrency(formatedAmount),
        amountPaid = CurrencyFormater.parseCurrency(formatedPaidAmount),
        categoryId = categoryId,
        personId = personId,
        date = parsedDate,
        description = description,
        isExpense = isExpense,
        transactionType = transactionType
    )
}