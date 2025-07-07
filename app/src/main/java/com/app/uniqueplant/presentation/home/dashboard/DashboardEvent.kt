package com.app.uniqueplant.presentation.home.dashboard

sealed class DashboardEvent {
    object LogoutClicked : DashboardEvent()
}