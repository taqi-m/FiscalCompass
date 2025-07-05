package com.app.uniqueplant.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.presentation.auth.AuthScreen
import com.app.uniqueplant.presentation.auth.AuthViewModel
import com.app.uniqueplant.presentation.auth.LoginScreen
import com.app.uniqueplant.presentation.auth.LoginViewModel
import com.app.uniqueplant.presentation.auth.SignUpScreen
import com.app.uniqueplant.presentation.auth.SignUpViewModel
import com.app.uniqueplant.presentation.home.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn()) Screen.Home.route else Screen.Auth.route
    ) {

        composable(route = Screen.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.state.collectAsState()

            AuthScreen(
                state = authState,
                onEvent = authViewModel::onEvent,
                isUserLoggedIn = authViewModel::isUserLoggedIn,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                })
        }

        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}
