package com.app.uniqueplant.presentation.screens.homeScreens.home

import androidx.navigation.NavHostController

sealed class HomeEvent {
    data class NavigateTo(val route: String) : HomeEvent()
    data class OnScreenLoad(val appNavController: NavHostController) : HomeEvent()
    object OnSettingsClicked : HomeEvent()
    object LogoutClicked : HomeEvent()
    object ToggleFabExpanded : HomeEvent()
}
