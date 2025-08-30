package com.app.uniqueplant.presentation.screens.home

import androidx.navigation.NavHostController
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen

data class HomeScreenState(
    val appNavController: NavHostController? = null,
    val selectedTab: String = HomeBottomScreen.Dashboard.route,
    val isFabExpanded: Boolean = false,
)
