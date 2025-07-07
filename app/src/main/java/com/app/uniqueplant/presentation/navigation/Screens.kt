package com.app.uniqueplant.presentation.navigation

import com.app.uniqueplant.R

sealed class MainScreens(val route: String) {
    object Auth : MainScreens("auth_screen")
    object Home : MainScreens("home_screen")
}

sealed class HomeBottomScreen(val route: String, val label: String, val iconResource: Int) {
    object Dashboard : HomeBottomScreen("dashboard", "Dashboard", R.drawable.ic_home_24)
    object Transactions : HomeBottomScreen("transactions", "Transactions", R.drawable.ic_list_24)
    object Analysis : HomeBottomScreen("analysis", "Analysis", R.drawable.ic_bar_chart_24)

    object Supervisor : HomeBottomScreen("users", "Users", R.drawable.ic_supervisor_24)
}