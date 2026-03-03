package com.fiscal.compass.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Comprehensive date/time utility object providing thread-safe operations for both
 * presentation and domain layers.
 *
 * All methods create new SimpleDateFormat instances per call to ensure thread safety.
 * All modification methods return new Long timestamps (immutable approach).
 */
@Suppress("unused")
object DateTimeUtil {

    // =============================================================================================
    // Format Constants
    // =============================================================================================

    const val DEFAULT_DATE_FORMAT = "dd MMMM yyyy"
    const val SHORT_DATE_FORMAT = "dd MMM, yyyy"
    const val API_DATE_FORMAT = "yyyy-MM-dd"
    const val DAY_MONTH_YEAR_FORMAT = "dd/MM/yyyy"
    const val MONTH_YEAR_FORMAT = "MMMM yyyy"
    const val DEFAULT_TIME_FORMAT = "hh:mm a"
    const val MILITARY_TIME_FORMAT = "HH:mm"
    const val API_TIME_FORMAT = "HH:mm:ss"
    const val DATETIME_FORMAT = "dd MMM, yyyy hh:mm a"

    // =============================================================================================
    // 1. Formatting Methods
    // =============================================================================================

    /**
     * Formats a Date object into a string based on the provided format pattern.
     *
     * @param date The Date object to format.
     * @param format The desired output format pattern (defaults to DEFAULT_DATE_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted date string.
     */
    fun formatDate(
        date: Date,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(date)
    }

    /**
     * Formats a Date object's time component into a string.
     *
     * @param date The Date object to format.
     * @param format The desired output format pattern (defaults to DEFAULT_TIME_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted time string.
     */
    fun formatTime(
        date: Date,
        format: String = DEFAULT_TIME_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(date)
    }

    /**
     * Formats a Date object with separate date and time formats.
     *
     * @param date The Date object to format.
     * @param dateFormat The date format pattern (defaults to SHORT_DATE_FORMAT).
     * @param timeFormat The time format pattern (defaults to DEFAULT_TIME_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted date-time string.
     */
    fun formatDateTime(
        date: Date,
        dateFormat: String = SHORT_DATE_FORMAT,
        timeFormat: String = DEFAULT_TIME_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        val combinedFormat = "$dateFormat $timeFormat"
        val sdf = SimpleDateFormat(combinedFormat, locale)
        return sdf.format(date)
    }

    /**
     * Formats a timestamp (milliseconds) into a string based on the provided format pattern.
     *
     * @param timestamp The timestamp in milliseconds.
     * @param format The desired output format pattern (defaults to DEFAULT_DATE_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted date-time string.
     */
    fun formatTimestamp(
        timestamp: Long,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(date)
    }

    /**
     * Formats a timestamp as a date string.
     *
     * @param timestamp The timestamp in milliseconds.
     * @param format The desired output format pattern (defaults to SHORT_DATE_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted date string.
     */
    fun formatTimestampAsDate(
        timestamp: Long,
        format: String = SHORT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        return formatTimestamp(timestamp, format, locale)
    }

    /**
     * Formats a timestamp as a time string.
     *
     * @param timestamp The timestamp in milliseconds.
     * @param format The desired output format pattern (defaults to DEFAULT_TIME_FORMAT).
     * @param locale The locale to use for formatting (defaults to system default).
     * @return A formatted time string.
     */
    fun formatTimestampAsTime(
        timestamp: Long,
        format: String = DEFAULT_TIME_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        return formatTimestamp(timestamp, format, locale)
    }

    // =============================================================================================
    // 2. Parsing Methods
    // =============================================================================================

    /**
     * Parses a date string into a Date object with error handling.
     *
     * @param dateString The string to parse.
     * @param format The format pattern of the input string.
     * @param locale The locale to use for parsing (defaults to system default).
     * @return Result containing the parsed Date or an exception.
     */
    fun parseDate(
        dateString: String,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): Result<Date> {
        return try {
            val sdf = SimpleDateFormat(format, locale)
            sdf.isLenient = false
            val date = sdf.parse(dateString)
            if (date != null) {
                Result.success(date)
            } else {
                Result.failure(IllegalArgumentException("Failed to parse date: $dateString"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Parses a date string into a timestamp with error handling.
     *
     * @param dateString The string to parse.
     * @param format The format pattern of the input string.
     * @param locale The locale to use for parsing (defaults to system default).
     * @return Result containing the parsed timestamp or an exception.
     */
    fun parseTimestamp(
        dateString: String,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): Result<Long> {
        return parseDate(dateString, format, locale).map { it.time }
    }

    /**
     * Parses a date string into a Date object, returning null on failure.
     *
     * @param dateString The string to parse.
     * @param format The format pattern of the input string.
     * @param locale The locale to use for parsing (defaults to system default).
     * @return The parsed Date or null if parsing fails.
     */
    fun parseDateOrNull(
        dateString: String,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): Date? {
        return parseDate(dateString, format, locale).getOrNull()
    }

    /**
     * Parses a date string into a timestamp, returning null on failure.
     *
     * @param dateString The string to parse.
     * @param format The format pattern of the input string.
     * @param locale The locale to use for parsing (defaults to system default).
     * @return The parsed timestamp or null if parsing fails.
     */
    fun parseTimestampOrNull(
        dateString: String,
        format: String = DEFAULT_DATE_FORMAT,
        locale: Locale = Locale.getDefault()
    ): Long? {
        return parseTimestamp(dateString, format, locale).getOrNull()
    }

    // =============================================================================================
    // 3. Current Date/Time Retrieval
    // =============================================================================================

    /**
     * Gets the current system timestamp in milliseconds.
     *
     * @return Current timestamp.
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Gets the current system date.
     *
     * @return Current Date object.
     */
    fun getCurrentDate(): Date {
        return Date()
    }

    /**
     * Gets the current system calendar.
     *
     * @return Current Calendar instance.
     */
    fun getCurrentCalendar(): Calendar {
        return Calendar.getInstance()
    }

    /**
     * Gets the current year.
     *
     * @return Current year (e.g., 2026).
     */
    fun getCurrentYear(): Int {
        return getCurrentCalendar().get(Calendar.YEAR)
    }

    /**
     * Gets the current month (0-based, where 0 = January).
     *
     * @return Current month (0-11).
     */
    fun getCurrentMonth(): Int {
        return getCurrentCalendar().get(Calendar.MONTH)
    }

    /**
     * Gets the current day of month.
     *
     * @return Current day of month (1-31).
     */
    fun getCurrentDayOfMonth(): Int {
        return getCurrentCalendar().get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Gets the current hour of day (24-hour format).
     *
     * @return Current hour (0-23).
     */
    fun getCurrentHourOfDay(): Int {
        return getCurrentCalendar().get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Gets the current minute.
     *
     * @return Current minute (0-59).
     */
    fun getCurrentMinute(): Int {
        return getCurrentCalendar().get(Calendar.MINUTE)
    }

    // =============================================================================================
    // 4. Field-Specific Modification
    // =============================================================================================

    /**
     * Sets the time (hour, minute, second) on a timestamp while preserving the date.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param hourOfDay The hour to set (0-23).
     * @param minute The minute to set (0-59).
     * @param second The second to set (0-59, defaults to 0).
     * @return New timestamp with updated time.
     */
    fun setTimeOnTimestamp(
        timestamp: Long,
        hourOfDay: Int,
        minute: Int,
        second: Int = 0
    ): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, second)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Sets the date (year, month, day) on a timestamp while preserving the time.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param year The year to set.
     * @param month The month to set (0-based, where 0 = January).
     * @param dayOfMonth The day of month to set (1-31).
     * @return New timestamp with updated date.
     */
    fun setDateOnTimestamp(
        timestamp: Long,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.timeInMillis
    }

    /**
     * Sets only the year on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param year The year to set.
     * @return New timestamp with updated year.
     */
    fun setYear(timestamp: Long, year: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.YEAR, year)
        return calendar.timeInMillis
    }

    /**
     * Sets only the month on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param month The month to set (0-based, where 0 = January).
     * @return New timestamp with updated month.
     */
    fun setMonth(timestamp: Long, month: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.MONTH, month)
        return calendar.timeInMillis
    }

    /**
     * Sets only the day of month on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param dayOfMonth The day of month to set (1-31).
     * @return New timestamp with updated day.
     */
    fun setDayOfMonth(timestamp: Long, dayOfMonth: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar.timeInMillis
    }

    /**
     * Sets only the hour of day on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param hourOfDay The hour to set (0-23).
     * @return New timestamp with updated hour.
     */
    fun setHourOfDay(timestamp: Long, hourOfDay: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        return calendar.timeInMillis
    }

    /**
     * Sets only the minute on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param minute The minute to set (0-59).
     * @return New timestamp with updated minute.
     */
    fun setMinute(timestamp: Long, minute: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }

    /**
     * Sets only the second on a timestamp, preserving all other fields.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param second The second to set (0-59).
     * @return New timestamp with updated second.
     */
    fun setSecond(timestamp: Long, second: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.SECOND, second)
        return calendar.timeInMillis
    }

    // =============================================================================================
    // 5. Date Boundary Methods (migrated from DateRange.kt)
    // =============================================================================================

    /**
     * Adjusts the timestamp to the start of the day (00:00:00.000).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the start of the day.
     */
    fun toStartOfDay(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Adjusts the timestamp to the end of the day (23:59:59.999).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the end of the day.
     */
    fun toEndOfDay(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    /**
     * Adjusts the timestamp to the start of the month (first day at 00:00:00.000).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the start of the month.
     */
    fun toStartOfMonth(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
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
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the end of the month.
     */
    fun toEndOfMonth(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    /**
     * Adjusts the timestamp to the start of the year (first day at 00:00:00.000).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the start of the year.
     */
    fun toStartOfYear(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Adjusts the timestamp to the end of the year (last day at 23:59:59.999).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Adjusted timestamp at the end of the year.
     */
    fun toEndOfYear(timestamp: Long): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // =============================================================================================
    // 6. Date Arithmetic
    // =============================================================================================

    /**
     * Adds the specified number of days to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param days The number of days to add (can be negative).
     * @return New timestamp with days added.
     */
    fun addDays(timestamp: Long, days: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.timeInMillis
    }

    /**
     * Adds the specified number of months to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param months The number of months to add (can be negative).
     * @return New timestamp with months added.
     */
    fun addMonths(timestamp: Long, months: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.MONTH, months)
        return calendar.timeInMillis
    }

    /**
     * Adds the specified number of years to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param years The number of years to add (can be negative).
     * @return New timestamp with years added.
     */
    fun addYears(timestamp: Long, years: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.YEAR, years)
        return calendar.timeInMillis
    }

    /**
     * Adds the specified number of weeks to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param weeks The number of weeks to add (can be negative).
     * @return New timestamp with weeks added.
     */
    fun addWeeks(timestamp: Long, weeks: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
        return calendar.timeInMillis
    }

    /**
     * Adds the specified number of hours to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param hours The number of hours to add (can be negative).
     * @return New timestamp with hours added.
     */
    fun addHours(timestamp: Long, hours: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.timeInMillis
    }

    /**
     * Adds the specified number of minutes to a timestamp.
     *
     * @param timestamp The original timestamp in milliseconds.
     * @param minutes The number of minutes to add (can be negative).
     * @return New timestamp with minutes added.
     */
    fun addMinutes(timestamp: Long, minutes: Int): Long {
        val calendar = timestampToCalendar(timestamp)
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.timeInMillis
    }

    // =============================================================================================
    // 7. Date Comparison
    // =============================================================================================

    /**
     * Checks if two timestamps represent the same day.
     *
     * @param timestamp1 First timestamp in milliseconds.
     * @param timestamp2 Second timestamp in milliseconds.
     * @return True if both timestamps are on the same day.
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = timestampToCalendar(timestamp1)
        val cal2 = timestampToCalendar(timestamp2)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Checks if two timestamps represent the same month and year.
     *
     * @param timestamp1 First timestamp in milliseconds.
     * @param timestamp2 Second timestamp in milliseconds.
     * @return True if both timestamps are in the same month.
     */
    fun isSameMonth(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = timestampToCalendar(timestamp1)
        val cal2 = timestampToCalendar(timestamp2)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }

    /**
     * Checks if two timestamps represent the same year.
     *
     * @param timestamp1 First timestamp in milliseconds.
     * @param timestamp2 Second timestamp in milliseconds.
     * @return True if both timestamps are in the same year.
     */
    fun isSameYear(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = timestampToCalendar(timestamp1)
        val cal2 = timestampToCalendar(timestamp2)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    /**
     * Checks if the first timestamp is before the second.
     *
     * @param timestamp1 First timestamp in milliseconds.
     * @param timestamp2 Second timestamp in milliseconds.
     * @return True if timestamp1 is before timestamp2.
     */
    fun isBefore(timestamp1: Long, timestamp2: Long): Boolean {
        return timestamp1 < timestamp2
    }

    /**
     * Checks if the first timestamp is after the second.
     *
     * @param timestamp1 First timestamp in milliseconds.
     * @param timestamp2 Second timestamp in milliseconds.
     * @return True if timestamp1 is after timestamp2.
     */
    fun isAfter(timestamp1: Long, timestamp2: Long): Boolean {
        return timestamp1 > timestamp2
    }

    /**
     * Checks if a timestamp falls between two other timestamps.
     *
     * @param timestamp The timestamp to check.
     * @param start The start of the range.
     * @param end The end of the range.
     * @param inclusive Whether to include the boundaries (defaults to true).
     * @return True if timestamp is between start and end.
     */
    fun isBetween(
        timestamp: Long,
        start: Long,
        end: Long,
        inclusive: Boolean = true
    ): Boolean {
        return if (inclusive) {
            timestamp in start..end
        } else {
            timestamp in (start + 1)..<end
        }
    }

    // =============================================================================================
    // 8. Conversion Utilities
    // =============================================================================================

    /**
     * Converts a timestamp to a Calendar instance.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Calendar instance set to the timestamp.
     */
    fun timestampToCalendar(timestamp: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar
    }

    /**
     * Converts a Calendar instance to a timestamp.
     *
     * @param calendar The Calendar instance.
     * @return Timestamp in milliseconds.
     */
    fun calendarToTimestamp(calendar: Calendar): Long {
        return calendar.timeInMillis
    }

    /**
     * Converts a Date object to a timestamp.
     *
     * @param date The Date object.
     * @return Timestamp in milliseconds.
     */
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    /**
     * Converts a timestamp to a Date object.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return Date object.
     */
    fun timestampToDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    /**
     * Converts a Calendar instance to a Date object.
     *
     * @param calendar The Calendar instance.
     * @return Date object.
     */
    fun calendarToDate(calendar: Calendar): Date {
        return calendar.time
    }

    /**
     * Converts a Date object to a Calendar instance.
     *
     * @param date The Date object.
     * @return Calendar instance set to the date.
     */
    fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    // =============================================================================================
    // 9. Component Extraction
    // =============================================================================================

    /**
     * Extracts the year from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The year.
     */
    fun getYear(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.YEAR)
    }

    /**
     * Extracts the month from a timestamp (0-based, where 0 = January).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The month (0-11).
     */
    fun getMonth(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.MONTH)
    }

    /**
     * Extracts the day of month from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The day of month (1-31).
     */
    fun getDayOfMonth(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Extracts the hour of day from a timestamp (24-hour format).
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The hour of day (0-23).
     */
    fun getHourOfDay(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Extracts the minute from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The minute (0-59).
     */
    fun getMinute(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.MINUTE)
    }

    /**
     * Extracts the second from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The second (0-59).
     */
    fun getSecond(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.SECOND)
    }

    /**
     * Extracts the day of week from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The day of week (1 = Sunday, 7 = Saturday).
     */
    fun getDayOfWeek(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.DAY_OF_WEEK)
    }

    /**
     * Extracts the day of year from a timestamp.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return The day of year (1-365/366).
     */
    fun getDayOfYear(timestamp: Long): Int {
        return timestampToCalendar(timestamp).get(Calendar.DAY_OF_YEAR)
    }

    // =============================================================================================
    // 10. Date Range Factory Methods
    // =============================================================================================

    /**
     * Creates a DateRange from optional nullable timestamps with boundary adjustments.
     *
     * @param start Optional start timestamp in milliseconds.
     * @param end Optional end timestamp in milliseconds.
     * @return DateRange with adjusted boundaries.
     */
    fun createDateRange(start: Long?, end: Long?): DateRange {
        return DateRange(
            startDate = start?.let { toStartOfDay(it) },
            endDate = end?.let { toEndOfDay(it) }
        )
    }

    /**
     * Creates a DateRange for the current month.
     *
     * @return DateRange spanning from start to end of current month.
     */
    fun getCurrentMonthRange(): DateRange {
        val currentTimestamp = getCurrentTimestamp()
        return DateRange(
            startDate = toStartOfMonth(currentTimestamp),
            endDate = toEndOfMonth(currentTimestamp)
        )
    }

    /**
     * Creates a DateRange for a specific month and year.
     *
     * @param year The year.
     * @param month The month (0-based, where 0 = January).
     * @return DateRange spanning the entire specified month.
     */
    fun getMonthRange(year: Int, month: Int): DateRange {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        val timestamp = calendar.timeInMillis
        return DateRange(
            startDate = toStartOfMonth(timestamp),
            endDate = toEndOfMonth(timestamp)
        )
    }

    /**
     * Creates a DateRange for a specific year.
     *
     * @param year The year.
     * @return DateRange spanning the entire specified year.
     */
    fun getYearRange(year: Int): DateRange {
        val calendar = Calendar.getInstance()
        calendar.set(year, Calendar.JANUARY, 1)
        val timestamp = calendar.timeInMillis
        return DateRange(
            startDate = toStartOfYear(timestamp),
            endDate = toEndOfYear(timestamp)
        )
    }

    /**
     * Creates a DateRange for the week containing the specified timestamp.
     * Week starts on Sunday.
     *
     * @param timestamp The timestamp in milliseconds.
     * @return DateRange spanning the entire week.
     */
    fun getWeekRange(timestamp: Long): DateRange {
        val calendar = timestampToCalendar(timestamp)

        // Set to first day of week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = toStartOfDay(calendar.timeInMillis)

        // Set to last day of week (Saturday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = toEndOfDay(calendar.timeInMillis)

        return DateRange(
            startDate = startOfWeek,
            endDate = endOfWeek
        )
    }
}

