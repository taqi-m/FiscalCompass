package com.fiscal.compass.domain.service.analytics

import android.os.Bundle

/**
 * Sealed class representing all analytics events in the app.
 * Each event defines its name and optional parameters.
 */
sealed class AnalyticsEvent(
    val eventName: String,
    val params: Bundle? = null
) {
    // ==================== Authentication Events ====================

    object LoginStarted : AnalyticsEvent("login_started")

    data class LoginSuccess(val method: String = "email") : AnalyticsEvent(
        eventName = "login_success",
        params = Bundle().apply {
            putString("method", method)
        }
    )

    data class LoginFailed(val errorMessage: String) : AnalyticsEvent(
        eventName = "login_failed",
        params = Bundle().apply {
            putString("error_message", errorMessage)
        }
    )

    object SignUpStarted : AnalyticsEvent("sign_up_started")

    data class SignUpSuccess(val method: String = "email") : AnalyticsEvent(
        eventName = "sign_up_success",
        params = Bundle().apply {
            putString("method", method)
        }
    )

    data class SignUpFailed(val errorMessage: String) : AnalyticsEvent(
        eventName = "sign_up_failed",
        params = Bundle().apply {
            putString("error_message", errorMessage)
        }
    )

    object Logout : AnalyticsEvent("logout")

    // ==================== Transaction Events ====================

    data class TransactionStarted(val type: String) : AnalyticsEvent(
        eventName = "transaction_started",
        params = Bundle().apply {
            putString("transaction_type", type)
        }
    )

    data class TransactionAdded(
        val type: String,
        val amount: Double,
        val categoryId: String,
        val hasDescription: Boolean,
        val hasPerson: Boolean
    ) : AnalyticsEvent(
        eventName = "transaction_added",
        params = Bundle().apply {
            putString("transaction_type", type)
            putDouble("amount", amount)
            putString("category_id", categoryId)
            putBoolean("has_description", hasDescription)
            putBoolean("has_person", hasPerson)
        }
    )

    data class TransactionViewed(val transactionId: String, val type: String) : AnalyticsEvent(
        eventName = "transaction_viewed",
        params = Bundle().apply {
            putString("transaction_id", transactionId)
            putString("transaction_type", type)
        }
    )

    data class TransactionDeleted(val type: String) : AnalyticsEvent(
        eventName = "transaction_deleted",
        params = Bundle().apply {
            putString("transaction_type", type)
        }
    )

    data class TransactionEdited(val type: String) : AnalyticsEvent(
        eventName = "transaction_edited",
        params = Bundle().apply {
            putString("transaction_type", type)
        }
    )

    // ==================== Category Events ====================

    data class CategoryCreated(val categoryName: String, val isExpenseCategory: Boolean) : AnalyticsEvent(
        eventName = "category_created",
        params = Bundle().apply {
            putString("category_name", categoryName)
            putBoolean("is_expense_category", isExpenseCategory)
        }
    )

    data class CategoryDeleted(val categoryId: String) : AnalyticsEvent(
        eventName = "category_deleted",
        params = Bundle().apply {
            putString("category_id", categoryId)
        }
    )

    data class CategoryEdited(val categoryId: String) : AnalyticsEvent(
        eventName = "category_edited",
        params = Bundle().apply {
            putString("category_id", categoryId)
        }
    )

    // ==================== Search Events ====================

    object SearchOpened : AnalyticsEvent("search_opened")

    data class SearchFiltersApplied(
        val hasTypeFilter: Boolean,
        val hasDateFilter: Boolean,
        val hasCategoryFilter: Boolean,
        val hasPersonFilter: Boolean,
        val categoryCount: Int,
        val personCount: Int
    ) : AnalyticsEvent(
        eventName = "search_filters_applied",
        params = Bundle().apply {
            putBoolean("has_type_filter", hasTypeFilter)
            putBoolean("has_date_filter", hasDateFilter)
            putBoolean("has_category_filter", hasCategoryFilter)
            putBoolean("has_person_filter", hasPersonFilter)
            putInt("category_count", categoryCount)
            putInt("person_count", personCount)
        }
    )

    data class SearchResultsViewed(val resultCount: Int) : AnalyticsEvent(
        eventName = "search_results_viewed",
        params = Bundle().apply {
            putInt("result_count", resultCount)
        }
    )

    object SearchFiltersCleared : AnalyticsEvent("search_filters_cleared")

    // ==================== Sync Events ====================

    object SyncStarted : AnalyticsEvent("sync_started")

    data class SyncCompleted(val itemsSynced: Int, val durationMs: Long) : AnalyticsEvent(
        eventName = "sync_completed",
        params = Bundle().apply {
            putInt("items_synced", itemsSynced)
            putLong("duration_ms", durationMs)
        }
    )

    data class SyncFailed(val errorMessage: String) : AnalyticsEvent(
        eventName = "sync_failed",
        params = Bundle().apply {
            putString("error_message", errorMessage)
        }
    )

    object AutoSyncTriggered : AnalyticsEvent("auto_sync_triggered")

    // ==================== Settings Events ====================

    data class ThemeChanged(val isDarkTheme: Boolean) : AnalyticsEvent(
        eventName = "theme_changed",
        params = Bundle().apply {
            putBoolean("is_dark_theme", isDarkTheme)
        }
    )

    data class SettingsChanged(val settingName: String, val value: String) : AnalyticsEvent(
        eventName = "settings_changed",
        params = Bundle().apply {
            putString("setting_name", settingName)
            putString("value", value)
        }
    )

    // ==================== Person Events ====================

    data class PersonCreated(val personName: String) : AnalyticsEvent(
        eventName = "person_created",
        params = Bundle().apply {
            putString("person_name", personName)
        }
    )

    data class PersonDeleted(val personId: String) : AnalyticsEvent(
        eventName = "person_deleted",
        params = Bundle().apply {
            putString("person_id", personId)
        }
    )

    // ==================== Navigation Events ====================

    object HomeViewed : AnalyticsEvent("home_viewed")

    object FabExpanded : AnalyticsEvent("fab_expanded")

    object FabCollapsed : AnalyticsEvent("fab_collapsed")

    // ==================== Report Events ====================

    data class ReportViewed(val reportType: String, val month: String) : AnalyticsEvent(
        eventName = "report_viewed",
        params = Bundle().apply {
            putString("report_type", reportType)
            putString("month", month)
        }
    )

    // ==================== Error Events ====================

    data class AppError(val errorType: String, val errorMessage: String) : AnalyticsEvent(
        eventName = "app_error",
        params = Bundle().apply {
            putString("error_type", errorType)
            putString("error_message", errorMessage)
        }
    )
}

