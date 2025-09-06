package com.app.uniqueplant.presentation.screens.homeScreens.home

import androidx.navigation.NavHostController
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.screens.categories.UiState

data class HomeScreenState(
    val uiState: UiState = UiState.Idle,
    val appNavController: NavHostController? = null,
    val selectedTab: String = HomeBottomScreen.Dashboard.route,
    val isFabExpanded: Boolean = false,
)