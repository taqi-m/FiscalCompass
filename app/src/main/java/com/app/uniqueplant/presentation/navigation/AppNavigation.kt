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
import com.app.uniqueplant.presentation.home.HomeScreen
import com.app.uniqueplant.presentation.home.HomeViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn()) {
            MainScreens.Home.route
        } else {
            MainScreens.Auth.route
        }
    ) {

        composable(route = MainScreens.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.state.collectAsState()

            AuthScreen(
                state = authState,
                onEvent = authViewModel::onEvent,
                onLoginSuccess = {
                    navController.navigate(MainScreens.Home.route) {
                        popUpTo(MainScreens.Auth.route) { inclusive = true }
                    }
                })
        }

        // TODO: handle view model injection properly
        composable(route = MainScreens.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }
    }
}
