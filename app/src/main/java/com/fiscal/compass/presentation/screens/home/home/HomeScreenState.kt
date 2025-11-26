package com.fiscal.compass.presentation.screens.home.home

import androidx.navigation.NavHostController
import com.fiscal.compass.presentation.navigation.HomeBottomScreen
import com.fiscal.compass.presentation.screens.category.UiState

data class HomeScreenState(
    val uiState: UiState = UiState.Idle,
    val appNavController: NavHostController? = null,
    val selectedTab: String = HomeBottomScreen.Dashboard.route,
    val isFabExpanded: Boolean = false,
    val canViewCategories: Boolean = false,
    val canViewPeople: Boolean = false
)