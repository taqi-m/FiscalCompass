package com.fiscal.compass.presentation.screens.home.home

import androidx.navigation.NavHostController

sealed class HomeEvent {
    object ToggleFabExpanded : HomeEvent()
}
