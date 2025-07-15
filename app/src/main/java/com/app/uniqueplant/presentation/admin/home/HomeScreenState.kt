package com.app.uniqueplant.presentation.admin.home

import com.app.uniqueplant.presentation.navigation.HomeBottomScreen

data class HomeScreenState(
    val selectedTab: String = HomeBottomScreen.Dashboard.route,
    val isFabExpanded: Boolean = false,
)
