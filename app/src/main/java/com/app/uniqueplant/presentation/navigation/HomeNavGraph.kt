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
import com.app.uniqueplant.presentation.admin.dashboard.DashboardScreen
import com.app.uniqueplant.presentation.admin.dashboard.DashboardViewModel
import com.app.uniqueplant.presentation.admin.supervisor.SupervisorScreen
import com.app.uniqueplant.presentation.admin.transaction.TransactionsScreen

// Placeholder for NavGraph


@Composable
fun HomeNavGraph(homeNavController: NavHostController, appNavController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(homeNavController, startDestination = HomeBottomScreen.Dashboard.route, modifier = modifier) {
        composable(HomeBottomScreen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            val state by dashboardViewModel.state.collectAsState()
            DashboardScreen(
                state = state,
                onEvent = dashboardViewModel::onEvent,
                onLogout = { route ->
                    appNavController.navigate(MainScreens.Auth.route) {
                        popUpTo(route) { inclusive = true }
                    }
                },
            )
        }
        composable(HomeBottomScreen.Transactions.route) {
            TransactionsScreen()
        }
        composable(HomeBottomScreen.Analytics.route) {
            AnalyticsScreen()
        }

        composable(HomeBottomScreen.Supervisor.route){
            SupervisorScreen()
        }
    }
}
