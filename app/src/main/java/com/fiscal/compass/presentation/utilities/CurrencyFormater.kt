package com.fiscal.compass.presentation.utilities

import java.util.Locale

private const val INTEGER_FORMAT = "%,d"
private const val DECIMAL_FORMAT = "%,.2f"
private const val CALCULATOR_INTEGER_FORMAT = "%,.0f"

/**
 * A utility object for formatting and parsing currency values.
 */
object CurrencyFormater {

    /**
     * Formats a given amount as a currency string.
     *
     * @param amount The amount to format.
     * @param currencySymbol The currency symbol to prepend (default is "Rs. ").
     * @return A formatted currency string.
     */
    fun formatCurrency(amount: Double, currencySymbol: String = "Rs. "): String {
        val formattedAmount = INTEGER_FORMAT.format(amount.toLong())
        return "$currencySymbol$formattedAmount"
    }

    /**
     * Formats a string representation of an amount into a currency string for calculator display.
     * It handles both integer and decimal values.
     * @param amount The string amount to format.
     * @param currencySymbol The currency symbol to prepend. Defaults to "Rs. ".
     * @return A formatted currency string.
     */
    fun formatCalculatorCurrency(amount: String, currencySymbol: String = "Rs. "): String {
        val numericAmount = amount.replace(Regex("[^0-9.]"), "")
        val formattedAmount = if (numericAmount.isEmpty()) {
            "0"
        } else {
            String.format(Locale.getDefault(), CALCULATOR_INTEGER_FORMAT, numericAmount.toDoubleOrNull() ?: 0.0)
        }
        return "$currencySymbol$formattedAmount"
    }

    /**
     * Parses a formatted currency string back into a Double.
     * It strips out any non-numeric characters except for the decimal point.
     * @param formattedAmount The currency string to parse.
     * @return The parsed Double value, or 0.0 if parsing fails.
     */
    fun parseCurrency(formattedAmount: String): Double {
        return formattedAmount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
    }
}
