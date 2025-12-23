package com.fiscal.compass.presentation.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    const val DEFAULT_DATE_FORMAT = "dd MMMM yyyy"
    const val API_DATE_FORMAT = "yyyy-MM-dd"
    const val DAY_MONTH_YEAR_FORMAT = "dd/MM/yyyy"
    const val DEFAULT_TIME_FORMAT = "hh:mm a"
    const val API_TIME_FORMAT = "HH:mm:ss"


    /**
     * Formats a Date object into a string based on the provided format pattern.
     *
     * @param date The Date object to format.
     * @param format The desired output format pattern (e.g., "dd MMMM yyyy").
     * @param locale The locale to use for formatting (defaults to Locale.getDefault()).
     * @return A formatted date string.
     */
    fun formatDate(date: Date, format: String = DEFAULT_DATE_FORMAT, locale: Locale = Locale.getDefault()): String {
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(date)
    }

    fun provideFormattedTime(date: Date, format: String = DEFAULT_TIME_FORMAT): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }
}