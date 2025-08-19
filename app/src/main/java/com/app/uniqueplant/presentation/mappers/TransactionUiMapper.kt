package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.presentation.model.TransactionUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "hh:mm a"
fun formatDate(date: Date): String {
    return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date)
}

fun formatTime(date: Date): String {
    return SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date)
}

fun Transaction.toTransactionUi(): TransactionUi {
    return TransactionUi(
        transactionId = transactionId,
        formatedAmount = CurrencyFormaterUseCase.formatCurrency(amount),
        formatedDate = formatDate(date),
        formatedTime = formatTime(date),
        description = description,
        isExpense = isExpense,
        transactionType = transactionType
    )
}

fun TransactionUi.toTransaction(): Transaction {
    return Transaction(
        transactionId = transactionId,
        amount = CurrencyFormaterUseCase.parseCurrency(formatedAmount),
        date = Date(),
        description = description,
        isExpense = isExpense,
        transactionType = transactionType
    )
}