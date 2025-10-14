package com.app.uniqueplant.presentation.screens.homeScreens.dashboard

import androidx.navigation.NavHostController


sealed class DashboardEvent {
    object OnAddTransactionClicked : DashboardEvent()
    data class OnScreenLoad(val appNavController: NavHostController) : DashboardEvent()
    object OnCategoriesClicked : DashboardEvent()
    object OnPersonsClicked : DashboardEvent()
    object OnJobsClicked : DashboardEvent()
    object OnSynClicked : DashboardEvent() {

    }
}
