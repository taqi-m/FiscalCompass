package com.app.uniqueplant.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository
import com.app.uniqueplant.presentation.admin.JobsScreen
import com.app.uniqueplant.presentation.admin.JobsViewModel
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


// Animation duration constant for consistent transitions
private const val TRANSITION_DURATION = 500

// Pre-defined transition animations
private val enterFromLeft = fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
        slideInHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> -fullWidth }

private val enterFromRight = fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
        slideInHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> fullWidth }

private val enterFromUp = fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
        slideInVertically(animationSpec = tween(TRANSITION_DURATION)) { fullHeight -> fullHeight }

private val exitToLeft = fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
        slideOutHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> -fullWidth }

private val exitToRight = fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
        slideOutHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> fullWidth }

private val exitToDown = fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
        slideOutVertically(animationSpec = tween(TRANSITION_DURATION)) { fullHeight -> fullHeight }

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

        composable(
            route = MainScreens.Auth.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }) { backStackEntry ->
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

        composable(
            route = MainScreens.Home.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }) { backStackEntry ->
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

        composable(
            route = MainScreens.AdminHome.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.AddTransaction.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToLeft }) { backStackEntry ->
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(backStackEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()
            AddTransactionScreen(
                appNavController = navController,
                state = addTransactionState,
                onEvent = addTransactionViewModel::onEvent
            )
        }

        composable(
            MainScreens.Settings.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) { backStackEntry ->
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

        composable(
            route = MainScreens.Categories.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) { backStackEntry ->
            val categoriesViewModel: CategoriesViewModel = hiltViewModel(backStackEntry)
            val state by categoriesViewModel.state.collectAsState()
            CategoriesScreen(
                state = state,
                onEvent = categoriesViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.Person.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) {
            val personViewModel: PersonViewModel = hiltViewModel()
            val state by personViewModel.state.collectAsState()
            PersonScreen(
                state = state,
                onEvent = personViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.Jobs.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) {
            val jobsViewModel: JobsViewModel = hiltViewModel()
            val state by jobsViewModel.state.collectAsState()
            JobsScreen(
                state = state,
                onEvent = jobsViewModel::onEvent
            )
        }


    }
}
