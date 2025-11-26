package com.fiscal.compass.presentation.screens.home.home

import androidx.navigation.NavHostController

sealed class HomeEvent {
    data class NavigateTo(val route: String) : HomeEvent()
    data class OnScreenLoad(val appNavController: NavHostController) : HomeEvent()
    object OnSettingsClicked : HomeEvent()
    object OnSearchClicked : HomeEvent()
    object LogoutClicked : HomeEvent()
    object ToggleFabExpanded : HomeEvent()
}
