package com.fiscal.compass.domain.util

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
            return DateTimeUtil.createDateRange(start, end)
        }

        /**
         * Creates a DateRange for the current month.
         *
         * @return DateRange spanning from start to end of current month
         */
        fun currentMonth(): DateRange {
            return DateTimeUtil.getCurrentMonthRange()
        }

        /**
         * Creates a DateRange for a specific month and year.
         *
         * @param year The year
         * @param month The month (0-based, where 0 = January)
         * @return DateRange spanning the entire specified month
         */
        fun forMonth(year: Int, month: Int): DateRange {
            return DateTimeUtil.getMonthRange(year, month)
        }
    }
}

