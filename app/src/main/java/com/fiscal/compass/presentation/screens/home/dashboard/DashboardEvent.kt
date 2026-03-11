package com.fiscal.compass.presentation.screens.home.dashboard

sealed class DashboardEvent {
    object OnScreenLoad : DashboardEvent()
    object OnAddTransactionClicked : DashboardEvent()
    object OnCategoriesClicked : DashboardEvent()
    object OnPersonsClicked : DashboardEvent()
    object OnJobsClicked : DashboardEvent()
    object OnSynClicked : DashboardEvent()
}
