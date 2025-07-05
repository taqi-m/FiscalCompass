package com.app.uniqueplant.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object Home : Screen("home_screen")
}
