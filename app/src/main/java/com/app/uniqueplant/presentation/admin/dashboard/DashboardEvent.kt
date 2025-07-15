package com.app.uniqueplant.presentation.admin.dashboard

sealed class DashboardEvent {
    object LogoutClicked : DashboardEvent()
    object AddExpenseClicked : DashboardEvent()
}