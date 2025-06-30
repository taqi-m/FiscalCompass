package com.app.uniqueplant.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object SignUp : Screen("sign_up_screen")
}
