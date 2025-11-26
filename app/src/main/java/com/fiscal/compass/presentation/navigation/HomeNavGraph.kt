package com.fiscal.compass.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiscal.compass.presentation.screens.category.CategoriesScreen
import com.fiscal.compass.presentation.screens.category.CategoriesViewModel
import com.fiscal.compass.presentation.screens.home.analytics.AnalyticsScreen
import com.fiscal.compass.presentation.screens.home.analytics.AnalyticsViewModel
import com.fiscal.compass.presentation.screens.home.dashboard.DashboardScreen
import com.fiscal.compass.presentation.screens.home.dashboard.DashboardViewModel
import com.fiscal.compass.presentation.screens.person.PersonScreen
import com.fiscal.compass.presentation.screens.person.PersonViewModel

private const val TRANSITION_DURATION = 300

private val defaultTransitionSpec = tween<Float>(
    durationMillis = TRANSITION_DURATION,
    easing = FastOutSlowInEasing
)


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
        enterTransition = {
            fadeIn(
                animationSpec = defaultTransitionSpec
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = defaultTransitionSpec
            )
        }
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
            route = HomeBottomScreen.Analytics.route,

            ) { backStackEntry ->
            val analyticsViewModel: AnalyticsViewModel = hiltViewModel(backStackEntry)
            val state by analyticsViewModel.state.collectAsState()
            AnalyticsScreen(
                state = state,
                onEvent = analyticsViewModel::onEvent
            )
        }

        composable(
            route = HomeBottomScreen.Categories.route
        ) { backStackEntry ->
            val categoriesViewModel: CategoriesViewModel = hiltViewModel(backStackEntry)
            val state by categoriesViewModel.state.collectAsState()
            CategoriesScreen(
                appNavController = appNavController,
                state = state,
                onEvent = categoriesViewModel::onEvent
            )
        }

        composable(
            route = HomeBottomScreen.People.route
        ){ backStackEntry ->
            val personViewModel: PersonViewModel = hiltViewModel()
            val state by personViewModel.state.collectAsState()
            PersonScreen(
                state = state,
                onEvent = personViewModel::onEvent,
            )
        }
    }
}
