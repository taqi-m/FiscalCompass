package com.app.uniqueplant.presentation.screens.search

sealed class SearchEvent {
    object OnFilterIconClicked : SearchEvent()
    object OnDismissFilterDialog : SearchEvent()
    data class UpdateFilterType(val type: String?) : SearchEvent()
    data class SubmitFilterCategory(val categoryId: Long) : SearchEvent()
    data class SubmitFilterPerson(val personId: Long) : SearchEvent()
    data class UpdateFilterDateRange(val startDate: Long?, val endDate: Long?) : SearchEvent()
    data class UpdateCategorySearchQuery(val query: String) : SearchEvent()
    data class UpdatePersonSearchQuery(val query: String) : SearchEvent()
    data class ShowCategoryDropdown(val show: Boolean) : SearchEvent()
    data class ShowPersonDropdown(val show: Boolean) : SearchEvent()
    object ApplyFilters : SearchEvent()
    object ClearFilters : SearchEvent()
}
