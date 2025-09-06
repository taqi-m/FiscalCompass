package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.ExpenseWithCategoryAndPerson
import com.app.uniqueplant.domain.usecase.CurrencyFormaterUseCase
import com.app.uniqueplant.presentation.model.ExpenseUi
import com.app.uniqueplant.presentation.model.ExpenseWithCategoryAndPersonUi
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT = "dd MM, yyyy"
private const val TIME_FORMAT = "HH:mm"

fun Expense.toUi(): ExpenseUi {
    return ExpenseUi(
        expenseId = expenseId,
        formatedAmount = CurrencyFormaterUseCase.formatCurrency(amount),
        formatedDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date),
        formatedTime = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date),
        description = description
    )
}

fun ExpenseWithCategoryAndPerson.toUi(): ExpenseWithCategoryAndPersonUi {
    return ExpenseWithCategoryAndPersonUi(
        expense = expense.toUi(),
        category = category?.toUi(),
        person = person?.toUi()
    )
}