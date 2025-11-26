package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.ExpenseFull
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import com.fiscal.compass.presentation.model.ExpenseUi
import com.fiscal.compass.presentation.model.ExpenseWithCategoryAndPersonUi
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Expense.toUi(): ExpenseUi {
    return ExpenseUi(
        expenseId = expenseId,
        formatedAmount = CurrencyFormater.formatCurrency(amount),
        formatedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date),
        formatedTime = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date),
        description = description
    )
}

fun ExpenseFull.toUi(): ExpenseWithCategoryAndPersonUi {
    return ExpenseWithCategoryAndPersonUi(
        expense = expense.toUi(),
        category = category?.toUi(),
        person = person?.toUi()
    )
}