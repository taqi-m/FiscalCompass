package com.fiscal.compass.domain.util

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person


/**
 * Represents the criteria for searching transactions.
 *
 * This immutable data class holds various filters that can be applied when searching for transactions.
 * It includes filters for transaction type, a specific date range, associated persons, and categories.
 * Use the copy-based builder methods to create modified versions of the criteria.
 *
 * @property transactionType The type of transaction to filter by (e.g., income, expense).
 * @property dateRange The date range within which to search for transactions.
 * @property persons The list of persons associated with the transactions to filter by.
 * @property categories The list of categories associated with the transactions to filter by.
 */
data class SearchCriteria(
    val transactionType: TransactionType? = null,
    val dateRange: DateRange? = null,
    val persons: List<Person>? = null,
    val categories: List<Category>? = null
) {
    /**
     * Returns a new SearchCriteria with the specified transaction type.
     * @param transactionType The transaction type to set.
     */
    fun withTransactionType(transactionType: TransactionType?): SearchCriteria {
        return copy(transactionType = transactionType)
    }

    /**
     * Returns a new SearchCriteria with the specified date range.
     * @param dateRange The date range to set.
     */
    fun withDateRange(dateRange: DateRange?): SearchCriteria {
        return copy(dateRange = dateRange)
    }

    /**
     * Returns a new SearchCriteria with the specified persons.
     * @param persons The list of persons to set.
     */
    fun withPersons(persons: List<Person>?): SearchCriteria {
        return copy(persons = persons)
    }

    /**
     * Returns a new SearchCriteria with an additional person added.
     * @param person The person to add.
     */
    fun withAddedPerson(person: Person): SearchCriteria {
        val currentPersons = persons ?: emptyList()
        return copy(persons = currentPersons + person)
    }


    /**
     * Returns a new SearchCriteria with the specified categories.
     * @param categories The list of categories to set.
     */
    fun withCategories(categories: List<Category>?): SearchCriteria {
        return copy(categories = categories)
    }

    /**
     * Returns a new SearchCriteria with an additional category added.
     * @param category The category to add.
     */
    fun withAddedCategory(category: Category): SearchCriteria {
        val currentCategories = categories ?: emptyList()
        return copy(categories = currentCategories + category)
    }

    /**
     * Gets the list of person IDs for the search criteria.
     * @return The list of person IDs, or an empty list if not set.
     */
    fun getPersonIds(): List<String> {
        return persons?.map { it.personId } ?: emptyList()
    }

    /**
     * Gets the list of category IDs for the search criteria.
     * @return The list of category IDs, or an empty list if not set.
     */
    fun getCategoryIds(): List<String> {
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
}