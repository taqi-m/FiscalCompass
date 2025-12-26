package com.fiscal.compass.domain.util

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person


/**
 * Represents the criteria for searching transactions.
 *
 * This data class holds various filters that can be applied when searching for transactions.
 * It includes filters for transaction type, a specific date range, associated persons, and categories.
 * The class provides methods to get, set, and add to these criteria, as well as utility methods
 * to extract IDs and check if any filters are active.
 *
 * @property transactionType The type of transaction to filter by (e.g., income, expense).
 * @property dateRange The date range within which to search for transactions.
 * @property persons The list of persons associated with the transactions to filter by.
 * @property categories The list of categories associated with the transactions to filter by.
 */
data class SearchCriteria(
    private var transactionType : TransactionType? = null,
    private var dateRange: DateRange? = null,
    private var persons: List<Person>? = null,
    private var categories: List<Category>? = null
) {
    /**
     * Gets the transaction type for the search criteria.
     * @return The transaction type, or null if not set.
     */
    fun getTransactionType(): TransactionType? {
        return transactionType
    }

    /**
     * Sets the transaction type for the search criteria.
     * @param transactionType The transaction type to set.
     */
    fun setTransactionType(transactionType: TransactionType?) {
        this.transactionType = transactionType
    }

    /**
     * Gets the date range for the search criteria.
     * @return The date range, or null if not set.
     */
    fun getDateRange(): DateRange? {
        return dateRange
    }

    /**
     * Sets the date range for the search criteria.
     * @param dateRange The date range to set.
     */
    fun setDateRange(dateRange: DateRange?) {
        this.dateRange = dateRange
    }

    /**
     * Gets the list of persons for the search criteria.
     * @return The list of persons, or null if not set.
     */
    fun getPersons(): List<Person>? {
        return persons
    }

    /**
     * Sets the list of persons for the search criteria.
     * @param persons The list of persons to set.
     */
    fun setPersons(persons: List<Person>?) {
        this.persons = persons
    }

    /**
     * Adds a single person to the list of persons for the search criteria.
     * @param person The person to add.
     */
    fun addPerson(person: Person) {
        if (this.persons == null) {
            this.persons = mutableListOf()
        }
        (this.persons as MutableList<Person>).add(person)
    }

    /**
     * Gets the list of categories for the search criteria.
     * @return The list of categories, or null if not set.
     */
    fun getCategories(): List<Category>? {
        return categories
    }

    /**
     * Sets the list of categories for the search criteria.
     * @param categories The list of categories to set.
     */
    fun setCategories(categories: List<Category>?) {
        this.categories = categories
    }

    /**
     * Adds a single category to the list of categories for the search criteria.
     * @param category The category to add.
     */
    fun addCategory(category: Category) {
        if (this.categories == null) {
            this.categories = mutableListOf()
        }
        (this.categories as MutableList<Category>).add(category)
    }

    /**
     * Gets the list of person IDs for the search criteria.
     * @return The list of person IDs, or an empty list if not set.
     */
    fun getPersonIds(): List<Long> {
        return persons?.map { it.personId } ?: emptyList()
    }

    /**
     * Gets the list of category IDs for the search criteria.
     * @return The list of category IDs, or an empty list if not set.
     */
    fun getCategoryIds(): List<Long> {
        return categories?.map { it.categoryId } ?: emptyList()
    }

    /**
     * Checks if any search filters are currently active.
     * @return True if any filter is set, false otherwise.
     */
    fun areAnyFiltersActive(): Boolean {
        return transactionType != null ||
                dateRange != null ||
                !persons.isNullOrEmpty() ||
                !categories.isNullOrEmpty()
    }

    /**
     * Sets the date range to the current month (from start to end of month).
     */
    fun setDateRangeToCurrentMonth() {
        this.dateRange = DateRange.currentMonth()
    }

    /**
     * Sets the date range to a specific month and year.
     * @param year The year
     * @param month The month (0-based, where 0 = January)
     */
    fun setDateRangeToMonth(year: Int, month: Int) {
        this.dateRange = DateRange.forMonth(year, month)
    }

    /**
     * Sets the date range start date to the beginning of the month for the given timestamp.
     * @param timestamp The timestamp to adjust to start of month
     */
    fun setStartDateToBeginningOfMonth(timestamp: Long) {
        val adjustedStart = timestamp.toStartOfMonth()
        val currentEnd = dateRange?.endDate
        this.dateRange = DateRange(adjustedStart, currentEnd)
    }

    /**
     * Sets the date range end date to the end of the month for the given timestamp.
     * @param timestamp The timestamp to adjust to end of month
     */
    fun setEndDateToEndOfMonth(timestamp: Long) {
        val adjustedEnd = timestamp.toEndOfMonth()
        val currentStart = dateRange?.startDate
        this.dateRange = DateRange(currentStart, adjustedEnd)
    }

    /**
     * Convenience method to create a full month range from any timestamp within that month.
     * @param timestamp Any timestamp within the desired month
     */
    fun setDateRangeToMonthOf(timestamp: Long) {
        this.dateRange = DateRange(
            timestamp.toStartOfMonth(),
            timestamp.toEndOfMonth()
        )
    }
}