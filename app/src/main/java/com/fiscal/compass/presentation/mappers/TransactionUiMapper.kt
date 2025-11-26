package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.TransactionUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val DATE_FORMAT = "dd MMM, yyyy"
private const val TIME_FORMAT = "hh:mm a"
fun formatDate(date: Date): String {
    return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date)
}

fun formatTime(date: Date): String {
    return SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date)
}

fun Transaction.toUi(): TransactionUi {
    return TransactionUi(
        transactionId = transactionId,
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        categoryId = categoryId,
        personId = personId,
        formatedDate = formatDate(date),
        formatedTime = formatTime(date),
        description = description,
        isExpense = isExpense,
        transactionType = transactionType
    )
}

fun TransactionUi.toTransaction(): Transaction {
    val dateTimeString = "$formatedDate $formatedTime"
    val dateFormat = SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT", Locale.getDefault())
    return Transaction(
        transactionId = transactionId,
        amount = CurrencyFormater.parseCurrency(formatedAmount),
        categoryId = categoryId,
        personId = personId,
        date = dateFormat.parse(dateTimeString) ?: Date(),
        description = description,
        isExpense = isExpense,
        transactionType = transactionType
    )
}