package com.app.uniqueplant.domain.usecase

import java.util.Locale

class CurrencyFormaterUseCase {
    companion object {
        fun formatCurrency(amount: Double, currencySymbol: String = "$ "): String {
            val formattedAmount = if (amount % 1 == 0.0) {
                "%,d".format(amount.toInt())
            } else {
                String.format(Locale.getDefault(), "%,.2f", amount)
            }
            return "$currencySymbol$formattedAmount"
        }

        fun formatCalculatorCurrency(amount: String, currencySymbol: String = "$ "): String {
            return "$currencySymbol$amount"
        }

        fun parseCurrency(formattedAmount: String): Double {
            return formattedAmount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        }
    }
}