package com.app.uniqueplant.presentation.home

sealed class HomeEvent {
    data class NavigateTo(val route: String) : HomeEvent()
    object LogoutClicked : HomeEvent()
}
