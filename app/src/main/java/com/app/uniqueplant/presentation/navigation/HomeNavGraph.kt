package com.app.uniqueplant.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.presentation.screens.home.analytics.AnalyticsScreen
import com.app.uniqueplant.presentation.screens.home.analytics.AnalyticsViewModel
import com.app.uniqueplant.presentation.screens.home.dashboard.DashboardScreen
import com.app.uniqueplant.presentation.screens.home.dashboard.DashboardViewModel
import com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions.TransactionViewModel
import com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions.TransactionsScreen
import com.google.gson.Gson


@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
    homeNavController: NavHostController,
    appNavController: NavHostController
) {
    NavHost(
        homeNavController,
        startDestination = HomeBottomScreen.Dashboard.route,
        modifier = modifier,
        enterTransition = { androidx.compose.animation.EnterTransition.None },
        exitTransition = { androidx.compose.animation.ExitTransition.None }
    ) {
        composable(
            route = HomeBottomScreen.Dashboard.route,

            ) { backStackEntry ->
            val dashboardViewModel: DashboardViewModel = hiltViewModel(backStackEntry)
            val state by dashboardViewModel.state.collectAsState()
            DashboardScreen(
                appNavController = appNavController,
                state = state,
                onEvent = dashboardViewModel::onEvent,
            )
        }

        composable(
            route = HomeBottomScreen.Transactions.route,

            ) { backStackEntry ->
            val transactionViewModel: TransactionViewModel = hiltViewModel(backStackEntry)
            val state by transactionViewModel.state.collectAsState()
            TransactionsScreen(
                state = state,
                onEvent = transactionViewModel::onEvent,
                onNavigateClicked = { transactionUi ->
                    val transaction = Uri.encode(Gson().toJson(transactionUi))
                    appNavController.navigate(
                        MainScreens.TransactionDetail.passTransaction(
                            transaction
                        )
                    )
                }
            )
        }

        composable(
            route = HomeBottomScreen.Analytics.route,

            ) { backStackEntry ->
            val analyticsViewModel: AnalyticsViewModel = hiltViewModel(backStackEntry)
            val state by analyticsViewModel.state.collectAsState()
            AnalyticsScreen(
                state = state,
                onEvent = analyticsViewModel::onEvent
            )
        }
    }
}
