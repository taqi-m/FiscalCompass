package com.app.uniqueplant.presentation.screens.search

sealed class SearchEvent {
    object OnFilterIconClicked : SearchEvent()
    object OnDismissFilterDialog : SearchEvent()
    data class UpdateFilterType(val type: String?) : SearchEvent()
    data class SubmitFilterCategory(val categoryId: Long) : SearchEvent()
    data class SubmitFilterPerson(val personId: Long) : SearchEvent()
    data class UpdateFilterDateRange(val startDate: Long?, val endDate: Long?) : SearchEvent()
    object ApplyFilters : SearchEvent()
    object ClearFilters : SearchEvent()
}
