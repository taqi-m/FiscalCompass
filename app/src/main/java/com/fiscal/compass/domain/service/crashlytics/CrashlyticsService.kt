package com.fiscal.compass.domain.service.crashlytics

/**
 * Abstraction layer for crash reporting.
 * Allows for easy swapping of crash reporting providers and no-op implementation for dev builds.
 */
interface CrashlyticsService {

    /**
     * Log a non-fatal exception
     */
    fun logException(throwable: Throwable)

    /**
     * Log a message to crashlytics
     */
    fun log(message: String)

    /**
     * Set the user identifier for crash reports
     */
    fun setUserId(userId: String?)

    /**
     * Set a custom key-value pair for crash reports
     */
    fun setCustomKey(key: String, value: String)

    /**
     * Set a custom key-value pair for crash reports (Boolean)
     */
    fun setCustomKey(key: String, value: Boolean)

    /**
     * Set a custom key-value pair for crash reports (Int)
     */
    fun setCustomKey(key: String, value: Int)

    /**
     * Set a custom key-value pair for crash reports (Long)
     */
    fun setCustomKey(key: String, value: Long)

    /**
     * Set a custom key-value pair for crash reports (Double)
     */
    fun setCustomKey(key: String, value: Double)

    /**
     * Record a caught exception with a custom message
     */
    fun recordException(message: String, throwable: Throwable)
}

