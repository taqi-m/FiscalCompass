package com.fiscal.compass.domain.util

import java.util.Calendar

/**
 * Represents a date range with optional start and end timestamps.
 *
 * @property startDate Optional start timestamp in milliseconds. Null represents no lower bound.
 * @property endDate Optional end timestamp in milliseconds. Null represents no upper bound.
 */
data class DateRange(
    val startDate: Long? = null,
    val endDate: Long? = null
) {
    companion object {
        /**
         * Creates a DateRange from optional nullable timestamps.
         *
         * @param start Optional start timestamp in milliseconds
         * @param end Optional end timestamp in milliseconds
         * @return DateRange with adjusted boundaries
         */
        fun from(start: Long?, end: Long?): DateRange {
            return DateRange(
                startDate = start?.toStartOfDay(),
                endDate = end?.toEndOfDay()
            )
        }

        /**
         * Creates a DateRange for the current month.
         *
         * @return DateRange spanning from start to end of current month
         */
        fun currentMonth(): DateRange {
            val calendar = Calendar.getInstance()
            return DateRange(
                startDate = calendar.timeInMillis.toStartOfMonth(),
                endDate = calendar.timeInMillis.toEndOfMonth()
            )
        }

        /**
         * Creates a DateRange for a specific month and year.
         *
         * @param year The year
         * @param month The month (0-based, where 0 = January)
         * @return DateRange spanning the entire specified month
         */
        fun forMonth(year: Int, month: Int): DateRange {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1)
            return DateRange(
                startDate = calendar.timeInMillis.toStartOfMonth(),
                endDate = calendar.timeInMillis.toEndOfMonth()
            )
        }
    }
}

/**
 * Adjusts the timestamp to the start of the day (00:00:00.000).
 *
 * @return Adjusted timestamp in milliseconds
 */
fun Long.toStartOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

/**
 * Adjusts the timestamp to the end of the day (23:59:59.999).
 *
 * @return Adjusted timestamp in milliseconds
 */
fun Long.toEndOfDay(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

/**
 * Adjusts the timestamp to the start of the month (first day at 00:00:00.000).
 *
 * @return Adjusted timestamp in milliseconds
 */
fun Long.toStartOfMonth(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

/**
 * Adjusts the timestamp to the end of the month (last day at 23:59:59.999).
 *
 * @return Adjusted timestamp in milliseconds
 */
fun Long.toEndOfMonth(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

