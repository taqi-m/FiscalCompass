package com.fiscal.compass.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.fiscal.compass.domain.service.analytics.AnalyticsService

/**
 * Composable that tracks screen views when the screen is displayed.
 * Should be called at the top level of each screen composable.
 */
@Composable
fun TrackScreenView(
    analyticsService: AnalyticsService,
    screenName: String,
    screenClass: String? = null
) {
    DisposableEffect(screenName) {
        analyticsService.logScreenView(screenName, screenClass)
        onDispose { }
    }
}

/**
 * Screen names for analytics tracking
 */
object AnalyticsScreens {
    const val AUTH = "auth_screen"
    const val HOME = "home_screen"
    const val ADD_TRANSACTION = "add_transaction_screen"
    const val AMOUNT = "amount_screen"
    const val TRANSACTION_DETAILS = "transaction_details_screen"
    const val CATEGORIES = "categories_screen"
    const val PERSONS = "persons_screen"
    const val JOBS = "jobs_screen"
    const val SETTINGS = "settings_screen"
    const val SYNC = "sync_screen"
    const val SEARCH_FILTERS = "search_filters_screen"
    const val SEARCH_RESULTS = "search_results_screen"
    const val ITEM_SELECTION = "item_selection_screen"
}

