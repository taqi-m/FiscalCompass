package com.app.uniqueplant.presentation.screens.homeScreens.analytics

sealed class AnalyticsEvent {
    data class LoadAnalytics(val month: Int, val year: Int) : AnalyticsEvent()
}