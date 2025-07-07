package com.app.uniqueplant.presentation.home

import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.navigation.MainScreens

data class HomeScreenState(
    val selectedTab: String = HomeBottomScreen.Dashboard.route,
)
