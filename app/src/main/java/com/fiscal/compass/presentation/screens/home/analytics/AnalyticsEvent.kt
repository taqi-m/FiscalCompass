package com.fiscal.compass.presentation.screens.home.analytics

sealed class AnalyticsEvent {
    data class LoadAnalytics(val month: Int, val year: Int) : AnalyticsEvent()
}