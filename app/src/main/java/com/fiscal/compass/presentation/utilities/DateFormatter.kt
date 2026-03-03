package com.fiscal.compass.presentation.utilities

import com.fiscal.compass.domain.util.DateTimeUtil
import java.util.Date
import java.util.Locale

/**
 * Presentation layer wrapper for DateTimeUtil.
 * Delegates all operations to DateTimeUtil for backward compatibility.
 *
 * @deprecated Use DateTimeUtil directly from domain.util package.
 */
object DateFormatter {
    const val DEFAULT_DATE_FORMAT = DateTimeUtil.DEFAULT_DATE_FORMAT
    const val API_DATE_FORMAT = DateTimeUtil.API_DATE_FORMAT
    const val DAY_MONTH_YEAR_FORMAT = DateTimeUtil.DAY_MONTH_YEAR_FORMAT
    const val DEFAULT_TIME_FORMAT = DateTimeUtil.DEFAULT_TIME_FORMAT
    const val API_TIME_FORMAT = DateTimeUtil.API_TIME_FORMAT

    /**
     * Formats a Date object into a string based on the provided format pattern.
     *
     * @param date The Date object to format.
     * @param format The desired output format pattern (e.g., "dd MMMM yyyy").
     * @param locale The locale to use for formatting (defaults to Locale.getDefault()).
     * @return A formatted date string.
     * @deprecated Use DateTimeUtil.formatDate() instead.
     */
    fun formatDate(date: Date, format: String = DEFAULT_DATE_FORMAT, locale: Locale = Locale.getDefault()): String {
        return DateTimeUtil.formatDate(date, format, locale)
    }

    /**
     * Formats a Date object's time component into a string.
     *
     * @param date The Date object to format.
     * @param format The desired output format pattern.
     * @return A formatted time string.
     * @deprecated Use DateTimeUtil.formatTime() instead.
     */
    fun provideFormattedTime(date: Date, format: String = DEFAULT_TIME_FORMAT): String {
        return DateTimeUtil.formatTime(date, format)
    }
}

