# Plan: Comprehensive DateTimeUtil in Domain Layer

Create a thread-safe, comprehensive date/time utility object named `DateTimeUtil` in the domain layer at `com.fiscal.compass.domain.util`, consolidating all date/time operations for both presentation and domain layers, with field-specific modification capabilities and immediate removal of `DateRange.kt` extension functions.

## Steps

1. Create new `DateTimeUtil` object in `domain.util` package (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\domain\util\DateTimeUtil.kt`) with all format constants (`SHORT_DATE_FORMAT`, `MONTH_YEAR_FORMAT`, `DATETIME_FORMAT`, `MILITARY_TIME_FORMAT`) and thread-safe `SimpleDateFormat` creation per call

2. Add formatting methods (`formatDate`, `formatTime`, `formatDateTime`, `formatTimestamp`, `formatTimestampAsDate`, `formatTimestampAsTime`) accepting `Date`, `Long`, or `Calendar` with optional format and locale parameters

3. Add parsing methods (`parseDate`, `parseTimestamp`, `parseDateOrNull`, `parseTimestampOrNull`) returning `Result<T>` or nullable types for safe string-to-date conversion with error handling

4. Add current date/time retrieval methods (`getCurrentTimestamp`, `getCurrentDate`, `getCurrentCalendar`, `getCurrentYear`, `getCurrentMonth`, `getCurrentDayOfMonth`, `getCurrentHourOfDay`, `getCurrentMinute`)

5. Add field-specific modification methods returning new `Long` timestamps (`setTimeOnTimestamp`, `setDateOnTimestamp`, `setYear`, `setMonth`, `setDayOfMonth`, `setHourOfDay`, `setMinute`, `setSecond`) that preserve unmodified fields

6. Migrate extension logic from `DateRange.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\domain\util\DateRange.kt`) as methods (`toStartOfDay`, `toEndOfDay`, `toStartOfMonth`, `toEndOfMonth`) accepting `Long` and returning new `Long`

7. Add date arithmetic methods (`addDays`, `addMonths`, `addYears`, `addWeeks`, `addHours`, `addMinutes`) and comparison utilities (`isSameDay`, `isSameMonth`, `isSameYear`, `isBefore`, `isAfter`, `isBetween`)

8. Add Calendar/Date conversion utilities (`timestampToCalendar`, `calendarToTimestamp`, `dateToTimestamp`, `timestampToDate`, `calendarToDate`, `dateToCalendar`) and component extraction (`getYear`, `getMonth`, `getDayOfMonth`, `getHourOfDay`, `getMinute`, `getDayOfWeek`)

9. Add date range factory methods in `DateTimeUtil` (`createDateRange`, `getCurrentMonthRange`, `getMonthRange`, `getYearRange`, `getWeekRange`) returning `DateRange` objects

10. Update `DateRange` companion object methods (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\domain\util\DateRange.kt`) to use `DateTimeUtil` instead of extension functions, then delete `toStartOfDay`, `toEndOfDay`, `toStartOfMonth`, `toEndOfMonth` extension functions

11. Update existing `DateFormatter` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\presentation\utilities\DateFormatter.kt`) to delegate to `DateTimeUtil` for backward compatibility or deprecate it entirely

12. Update all usages in mappers and UI components to use `DateTimeUtil`:
    - `TransactionUiMapper.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\presentation\mappers\TransactionUiMapper.kt`)
    - `ExpenseUiMapper.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\presentation\mappers\ExpenseUiMapper.kt`)
    - `IncomeUiMapper.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\presentation\mappers\IncomeUiMapper.kt`)
    - `DatePicker.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\ui\components\pickers\DatePicker.kt`)
    - `TimePicker.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\ui\components\pickers\TimePicker.kt`)
    - `SearchScreen.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\presentation\screens\search\SearchScreen.kt`)
    - `HorizontalSwitcher.kt` (`c:\Users\TAQI KHOKHAR\StudioProjects\FiscalCompass\app\src\main\java\com\fiscal\compass\ui\components\input\HorizontalSwitcher.kt`)

## Further Considerations

1. **DateFormatter disposition** - Should `DateFormatter` be kept as a thin wrapper delegating to `DateTimeUtil` for backward compatibility, or deprecated immediately and removed?

2. **Immutability approach** - All modification methods will return new `Long` timestamps (immutable, thread-safe) rather than modifying Calendar objects in-place

3. **Thread safety guarantee** - Each method creates a new `SimpleDateFormat` instance ensuring complete thread safety at the cost of performance (acceptable for typical usage patterns)

## Implementation Details

### Format Constants
```kotlin
const val DEFAULT_DATE_FORMAT = "dd MMMM yyyy"
const val SHORT_DATE_FORMAT = "dd MMM, yyyy"
const val API_DATE_FORMAT = "yyyy-MM-dd"
const val DAY_MONTH_YEAR_FORMAT = "dd/MM/yyyy"
const val MONTH_YEAR_FORMAT = "MMMM yyyy"
const val DEFAULT_TIME_FORMAT = "hh:mm a"
const val MILITARY_TIME_FORMAT = "HH:mm"
const val API_TIME_FORMAT = "HH:mm:ss"
const val DATETIME_FORMAT = "dd MMM, yyyy hh:mm a"
```

### Method Categories

#### 1. Formatting Methods
- `formatDate(date: Date, format: String, locale: Locale): String`
- `formatTime(date: Date, format: String, locale: Locale): String`
- `formatDateTime(date: Date, dateFormat: String, timeFormat: String, locale: Locale): String`
- `formatTimestamp(timestamp: Long, format: String, locale: Locale): String`
- `formatTimestampAsDate(timestamp: Long, format: String, locale: Locale): String`
- `formatTimestampAsTime(timestamp: Long, format: String, locale: Locale): String`

#### 2. Parsing Methods
- `parseDate(dateString: String, format: String, locale: Locale): Result<Date>`
- `parseTimestamp(dateString: String, format: String, locale: Locale): Result<Long>`
- `parseDateOrNull(dateString: String, format: String, locale: Locale): Date?`
- `parseTimestampOrNull(dateString: String, format: String, locale: Locale): Long?`

#### 3. Current Date/Time Retrieval
- `getCurrentTimestamp(): Long`
- `getCurrentDate(): Date`
- `getCurrentCalendar(): Calendar`
- `getCurrentYear(): Int`
- `getCurrentMonth(): Int`
- `getCurrentDayOfMonth(): Int`
- `getCurrentHourOfDay(): Int`
- `getCurrentMinute(): Int`

#### 4. Field-Specific Modification
- `setTimeOnTimestamp(timestamp: Long, hourOfDay: Int, minute: Int, second: Int = 0): Long`
- `setDateOnTimestamp(timestamp: Long, year: Int, month: Int, dayOfMonth: Int): Long`
- `setYear(timestamp: Long, year: Int): Long`
- `setMonth(timestamp: Long, month: Int): Long`
- `setDayOfMonth(timestamp: Long, dayOfMonth: Int): Long`
- `setHourOfDay(timestamp: Long, hourOfDay: Int): Long`
- `setMinute(timestamp: Long, minute: Int): Long`
- `setSecond(timestamp: Long, second: Int): Long`

#### 5. Date Boundary Methods (migrated from DateRange.kt)
- `toStartOfDay(timestamp: Long): Long`
- `toEndOfDay(timestamp: Long): Long`
- `toStartOfMonth(timestamp: Long): Long`
- `toEndOfMonth(timestamp: Long): Long`
- `toStartOfYear(timestamp: Long): Long`
- `toEndOfYear(timestamp: Long): Long`

#### 6. Date Arithmetic
- `addDays(timestamp: Long, days: Int): Long`
- `addMonths(timestamp: Long, months: Int): Long`
- `addYears(timestamp: Long, years: Int): Long`
- `addWeeks(timestamp: Long, weeks: Int): Long`
- `addHours(timestamp: Long, hours: Int): Long`
- `addMinutes(timestamp: Long, minutes: Int): Long`

#### 7. Date Comparison
- `isSameDay(timestamp1: Long, timestamp2: Long): Boolean`
- `isSameMonth(timestamp1: Long, timestamp2: Long): Boolean`
- `isSameYear(timestamp1: Long, timestamp2: Long): Boolean`
- `isBefore(timestamp1: Long, timestamp2: Long): Boolean`
- `isAfter(timestamp1: Long, timestamp2: Long): Boolean`
- `isBetween(timestamp: Long, start: Long, end: Long, inclusive: Boolean = true): Boolean`

#### 8. Conversion Utilities
- `timestampToCalendar(timestamp: Long): Calendar`
- `calendarToTimestamp(calendar: Calendar): Long`
- `dateToTimestamp(date: Date): Long`
- `timestampToDate(timestamp: Long): Date`
- `calendarToDate(calendar: Calendar): Date`
- `dateToCalendar(date: Date): Calendar`

#### 9. Component Extraction
- `getYear(timestamp: Long): Int`
- `getMonth(timestamp: Long): Int`
- `getDayOfMonth(timestamp: Long): Int`
- `getHourOfDay(timestamp: Long): Int`
- `getMinute(timestamp: Long): Int`
- `getSecond(timestamp: Long): Int`
- `getDayOfWeek(timestamp: Long): Int`
- `getDayOfYear(timestamp: Long): Int`

#### 10. Date Range Factory Methods
- `createDateRange(start: Long?, end: Long?): DateRange`
- `getCurrentMonthRange(): DateRange`
- `getMonthRange(year: Int, month: Int): DateRange`
- `getYearRange(year: Int): DateRange`
- `getWeekRange(timestamp: Long): DateRange`

## Migration Path

### Phase 1: Create DateTimeUtil
- Create the new `DateTimeUtil` object with all methods
- Ensure all methods are properly documented

### Phase 2: Update DateRange.kt
- Modify `DateRange.from()` to use `DateTimeUtil.toStartOfDay()` and `DateTimeUtil.toEndOfDay()`
- Modify `DateRange.currentMonth()` to use `DateTimeUtil.getCurrentMonthRange()`
- Modify `DateRange.forMonth()` to use `DateTimeUtil.getMonthRange()`
- Delete extension functions: `toStartOfDay`, `toEndOfDay`, `toStartOfMonth`, `toEndOfMonth`

### Phase 3: Update DateFormatter
- Keep `DateFormatter` as a thin wrapper that delegates to `DateTimeUtil`
- Add deprecation warnings if needed
- Or remove entirely if no external dependencies

### Phase 4: Update All Usages
- Replace `SimpleDateFormat` instances in mappers with `DateTimeUtil.formatTimestamp()`
- Replace `SimpleDateFormat` instances in UI components with `DateTimeUtil.formatTimestamp()`
- Update any Calendar manipulations to use `DateTimeUtil` methods

### Phase 5: Validation
- Verify all date formatting is consistent
- Verify all date calculations are correct
- Update documentation

