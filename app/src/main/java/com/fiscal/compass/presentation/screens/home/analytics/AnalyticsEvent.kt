package com.fiscal.compass.presentation.screens.home.analytics

sealed interface AnalyticsEvent {
    data object LoadAnalytics : AnalyticsEvent

    data class LoadAnalyticsForPeriod(
        val month: Int,
        val year: Int,
    ) : AnalyticsEvent
}