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
        val numberRegex = Regex("""-?\d+(?:,\d{3})*(?:\.\d+)?""")
        val match = numberRegex.find(formattedAmount)
        val raw = match?.value?.replace(",", "") ?: ""
        return raw.toDoubleOrNull() ?: 0.0
    }



/**
 * Extracts the first numeric substring (allows thousands separators and optional decimal part) from [input].
 *
 * Examples:
 * - Input: "Total: Rs. 1,234.56" -> returns "1,234.56"
 * - Input: "-¥2,000" -> returns "-2,000"
 * - Input: "no digits" -> returns "0"
 *
 * @param input Source string to extract the numeric value from.
 * @return The matched numeric substring or `"0"` if none found.
 */
fun cleanString(input: String): String {
    val numberRegex = Regex("""-?\d+(?:,\d{3})*(?:\.\d+)?""")
    val match = numberRegex.find(input)
    val raw = match?.value ?: "0"
    return raw
}

/**
 * Extracts the first integer-like numeric substring (allows thousands separators, no decimal part) from [input].
 *
 * Examples:
 * - Input: "Balance: 5,000.75" -> returns "5,000" (decimal part ignored)
 * - Input: "(-1,234)" -> returns "-1,234"
 * - Input: "abc" -> returns "0"
 *
 * @param input Source string to extract the integer numeric value from.
 * @return The matched integer numeric substring or `"0"` if none found.
 */
fun cleanNumericString(input: String): String {
    val numberRegex = Regex("""-?\d+(?:,\d{3})*""")
    val match = numberRegex.find(input)
    val raw = match?.value ?: "0"
    return raw
}
}
