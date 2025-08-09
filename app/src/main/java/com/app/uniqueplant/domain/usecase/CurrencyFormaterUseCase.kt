package com.app.uniqueplant.domain.usecase

import java.util.Locale

class CurrencyFormaterUseCase {
    companion object {
        fun formatCurrency(amount: Double, currencySymbol: String = "Rs. "): String {
            val formattedAmount = if (amount % 1 == 0.0) {
                "%,d".format(amount.toInt())
            } else {
                String.format(Locale.getDefault(), "%,.2f", amount)
            }
            return "$currencySymbol$formattedAmount"
        }

        fun formatCalculatorCurrency(amount: String, currencySymbol: String = "Rs. "): String {
            val numericAmount = amount.replace(Regex("[^0-9.]"), "")
            val formattedAmount = if (numericAmount.isEmpty()) {
                "0.00"
            } else {
                val parsedAmount = numericAmount.toDoubleOrNull() ?: 0.0
                if (parsedAmount % 1 == 0.0) {
                    String.format(Locale.getDefault(), "%,.0f", parsedAmount)
                } else {
                    String.format(Locale.getDefault(), "%,.2f", parsedAmount)
                }
            }
            return "$currencySymbol$formattedAmount"
        }

        fun parseCurrency(formattedAmount: String): Double {
            return formattedAmount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        }
    }
}