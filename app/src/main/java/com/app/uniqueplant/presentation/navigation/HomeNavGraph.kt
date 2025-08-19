package com.app.uniqueplant.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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

// Animation duration constant for consistent transitions
private const val TRANSITION_DURATION = 300

// Pre-defined transition animations
private val enterFromRight = fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
        slideInHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> -fullWidth }

private val enterFromLeft = fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
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
        composable(
            route = HomeBottomScreen.Dashboard.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToLeft }
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
            enterTransition = { enterFromLeft },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToRight }
        ) { backStackEntry ->
            val transactionViewModel: TransactionViewModel = hiltViewModel(backStackEntry)
            val state by transactionViewModel.state.collectAsState()
            TransactionsScreen(
                state = state,
                onEvent = transactionViewModel::onEvent
            )
        }

        composable(
            route = HomeBottomScreen.Analytics.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToRight }
        ) { backStackEntry ->
            val analyticsViewModel: AnalyticsViewModel = hiltViewModel(backStackEntry)
            val state by analyticsViewModel.state.collectAsState()
            AnalyticsScreen(
                state = state,
                onEvent = analyticsViewModel::onEvent
            )
        }

        composable(
            route = HomeBottomScreen.Supervisor.route,
            enterTransition = { enterFromUp },
            exitTransition = { exitToDown },
            popEnterTransition = { enterFromUp },
            popExitTransition = { exitToDown }
        ) { backStackEntry ->
            SupervisorScreen()
        }
    }
}
