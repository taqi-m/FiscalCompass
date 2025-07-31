package com.app.uniqueplant.presentation.admin.dashboard

sealed class DashboardEvent {
    object AddExpenseClicked : DashboardEvent()
}