package com.app.uniqueplant.presentation.admin.dashboard

import androidx.navigation.NavHostController


sealed class DashboardEvent {
    object AddExpenseClicked : DashboardEvent()
    object OnAddTransactionClicked : DashboardEvent()
    data class OnScreenLoad(val appNavController: NavHostController) : DashboardEvent()
    object OnCategoriesClicked : DashboardEvent()
    object OnPersonsClicked : DashboardEvent()
}
