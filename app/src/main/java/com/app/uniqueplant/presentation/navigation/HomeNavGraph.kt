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


@Composable
fun HomeNavGraph(
    homeNavController: NavHostController,
    appNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        homeNavController,
        startDestination = HomeBottomScreen.Dashboard.route,
        modifier = modifier
    ) {
        composable(HomeBottomScreen.Dashboard.route) { backStackEntry ->
            val dashboardViewModel: DashboardViewModel = hiltViewModel(backStackEntry)
            val state by dashboardViewModel.state.collectAsState()
            DashboardScreen(
                appNavController = appNavController,
                state = state,
                onEvent = dashboardViewModel::onEvent,
            )
        }
        composable(HomeBottomScreen.Transactions.route) { backStackEntry ->
            val transactionViewModel: TransactionViewModel = hiltViewModel(backStackEntry)
            val state by transactionViewModel.state.collectAsState()
            TransactionsScreen(
                state = state,
                onEvent = transactionViewModel::onEvent
            )
        }
        composable(HomeBottomScreen.Analytics.route) { backStackEntry ->
            val analyticsViewModel: AnalyticsViewModel = hiltViewModel(backStackEntry)
            val state by analyticsViewModel.state.collectAsState()
            AnalyticsScreen(
                state = state,
                onEvent = analyticsViewModel::onEvent
            )
        }

        composable(HomeBottomScreen.Supervisor.route){ backStackEntry ->
            SupervisorScreen()
        }
    }
}


