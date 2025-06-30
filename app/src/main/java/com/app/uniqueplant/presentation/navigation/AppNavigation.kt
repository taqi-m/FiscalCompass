package com.app.uniqueplant.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        startDestination = if (authViewModel.isUserLoggedIn()) Screen.Home.route else Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginState by loginViewModel.state.collectAsState()

            LoginScreen(
                state = loginState,
                onEvent = loginViewModel::onEvent,
                isUserLoggedIn = loginViewModel::isUserLoggedIn,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                }
            )
        }
        composable(route = Screen.SignUp.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            val signUpState by signUpViewModel.state.collectAsState()

            SignUpScreen(
                state = signUpState,
                onEvent = signUpViewModel::onEvent,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}
