package com.app.uniqueplant.presentation.admin.home

sealed class HomeEvent {
    data class NavigateTo(val route: String) : HomeEvent()
    object LogoutClicked : HomeEvent()
}
