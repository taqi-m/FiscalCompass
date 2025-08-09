package com.app.uniqueplant.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.presentation.admin.categories.CategoriesScreen
import com.app.uniqueplant.presentation.admin.categories.CategoriesViewModel
import com.app.uniqueplant.presentation.admin.home.HomeScreen
import com.app.uniqueplant.presentation.admin.home.HomeViewModel
import com.app.uniqueplant.presentation.admin.person.PersonScreen
import com.app.uniqueplant.presentation.admin.person.PersonViewModel
import com.app.uniqueplant.presentation.auth.AuthScreen
import com.app.uniqueplant.presentation.auth.AuthViewModel
import com.app.uniqueplant.presentation.settings.SettingsScreen
import com.app.uniqueplant.presentation.settings.SettingsViewModel
import com.app.uniqueplant.presentation.transactions.AddTransactionScreen
import com.app.uniqueplant.presentation.transactions.AddTransactionViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    prefs: SharedPreferencesRepository
) {

    NavHost(
        navController = navController,
        startDestination = if (prefs.isUserLoggedIn()) {
            when (prefs.getUserType()) {
                "admin" -> MainScreens.AdminHome.route
                "employee" -> MainScreens.EmployeeHome.route
                else -> MainScreens.Auth.route
            }
        } else {
            MainScreens.Auth.route
        }
    ) {

        composable(route = MainScreens.Auth.route) { backStackEntry ->
            val authViewModel: AuthViewModel = hiltViewModel(backStackEntry)
            val authState by authViewModel.state.collectAsState()

            AuthScreen(
                appNavController = navController,
                state = authState,
                onEvent = authViewModel::onEvent,
/*                onLoginSuccess = {
                    navController.navigate(MainScreens.Home.route) {
                        popUpTo(MainScreens.Auth.route) { inclusive = true }
                    }
                }*/
            )
        }

        composable(route = MainScreens.Home.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(route = MainScreens.EmployeeHome.route) {
            // User home screen content
        }

        composable(route = MainScreens.AdminHome.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(route = MainScreens.AddTransaction.route) { backStackEntry ->
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(backStackEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()
            AddTransactionScreen(
                appNavController = navController,
                state = addTransactionState,
                onEvent = addTransactionViewModel::onEvent
            )
        }

        composable(MainScreens.Settings.route) { backStackEntry ->
            val settingsViewModel: SettingsViewModel = hiltViewModel(backStackEntry)
            val state by settingsViewModel.state.collectAsState()
            SettingsScreen(
                state = state,
                onEvent = settingsViewModel::onEvent,
                onLogout = { route ->
                    navController.navigate(MainScreens.Auth.route) {
                        popUpTo(route) { inclusive = true }
                    }
                },
            )
        }

        composable(route = MainScreens.Categories.route) { backStackEntry ->
            val categoriesViewModel: CategoriesViewModel = hiltViewModel(backStackEntry)
            val state by categoriesViewModel.state.collectAsState()
            CategoriesScreen (
                state = state,
                onEvent = categoriesViewModel::onEvent
            )
        }

        composable(route = MainScreens.Person.route) {
            val personViewModel: PersonViewModel = hiltViewModel()
            val state by personViewModel.state.collectAsState()
            PersonScreen(
                state = state,
                onEvent = personViewModel::onEvent
            )
        }


    }
}
