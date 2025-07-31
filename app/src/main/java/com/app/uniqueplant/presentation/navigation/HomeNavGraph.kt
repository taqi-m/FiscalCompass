package com.app.uniqueplant.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.presentation.admin.analytics.AnalyticsScreen
import com.app.uniqueplant.presentation.admin.analytics.AnalyticsViewModel
import com.app.uniqueplant.presentation.admin.dashboard.DashboardScreen
import com.app.uniqueplant.presentation.admin.dashboard.DashboardViewModel
import com.app.uniqueplant.presentation.admin.supervisor.SupervisorScreen
import com.app.uniqueplant.presentation.admin.transaction.TransactionViewModel
import com.app.uniqueplant.presentation.admin.transaction.TransactionsScreen
import com.app.uniqueplant.presentation.settings.SettingsScreen
import com.app.uniqueplant.presentation.settings.SettingsViewModel



@Composable
fun HomeNavGraph(homeNavController: NavHostController, appNavController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(homeNavController, startDestination = HomeBottomScreen.Dashboard.route, modifier = modifier) {
        composable(HomeBottomScreen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            val state by dashboardViewModel.state.collectAsState()
            DashboardScreen(
                state = state,
                onEvent = dashboardViewModel::onEvent,
            )
        }
        composable(HomeBottomScreen.Transactions.route) {
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val state by transactionViewModel.state.collectAsState()
            TransactionsScreen(
                state = state,
                onEvent = transactionViewModel::onEvent
            )
        }
        composable(HomeBottomScreen.Analytics.route) {
            val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
            val state by analyticsViewModel.state.collectAsState()
            AnalyticsScreen(
                state = state,
                onEvent = analyticsViewModel::onEvent
            )
        }

        composable(HomeBottomScreen.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val state by settingsViewModel.state.collectAsState()
            SettingsScreen(
                state = state,
                onEvent = settingsViewModel::onEvent,
                onLogout = { route ->
                    appNavController.navigate(MainScreens.Auth.route) {
                        popUpTo(route) { inclusive = true }
                    }
                },
            )
        }

        composable(HomeBottomScreen.Supervisor.route){
            SupervisorScreen()
        }
    }
}


