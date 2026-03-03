package com.fiscal.compass.presentation.screens.search

import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.util.TransactionType

sealed class SearchEvent {
    // Filter events for temp criteria (uncommitted changes)
    data class UpdateTempFilterType(val type: TransactionType?) : SearchEvent()
    data class UpdateTempDateRange(val dateRange: DateRange?) : SearchEvent()
    data class TempStartDateSelected(val startDate: Long) : SearchEvent()
    data class TempEndDateSelected(val endDate: Long) : SearchEvent()

    // Selection update events (from navigation results)
    data class UpdateSelectedCategories(val categoryIds: List<String>) : SearchEvent()
    data class UpdateSelectedPersons(val personIds: List<String>) : SearchEvent()

    // Filter action events
    data object ApplyFilters : SearchEvent()
    data object ClearFilters : SearchEvent()
    data object ResetTempFilters : SearchEvent()
}
