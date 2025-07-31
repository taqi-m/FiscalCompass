package com.app.uniqueplant.presentation.admin.analytics

import java.time.Year

sealed class AnalyticsEvent {
    data class LoadAnalytics(val month: Int, val year: Int) : AnalyticsEvent()
}