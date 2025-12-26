package com.fiscal.compass.presentation.screens.search

import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.util.TransactionType

sealed class SearchEvent {
    object OnFilterIconClicked : SearchEvent()
    object OnDismissFilterDialog : SearchEvent()
    data class UpdateFilterType(val type: TransactionType?) : SearchEvent()
    data class UpdateDateRange(val dateRange: DateRange?) : SearchEvent()
    data class SubmitFilterCategory(val categoryId: Long) : SearchEvent()
    data class SubmitFilterPerson(val personId: Long) : SearchEvent()
    data class StartDateSelected(val startDate: Long) : SearchEvent()
    data class EndDateSelected(val endDate: Long) : SearchEvent()
    object NavigateToCategorySelection : SearchEvent()
    object NavigateToPersonSelection : SearchEvent()
    object ResetNavigation: SearchEvent()
    data class UpdateSelectedCategories(val categoryIds: List<Long>) : SearchEvent()
    data class UpdateSelectedPersons(val personIds: List<Long>) : SearchEvent()
    object ApplyFilters : SearchEvent()
    object ClearFilters : SearchEvent()
}
