package com.fiscal.compass.domain.model.update

/**
 * Represents the current state of the update check flow.
 */
sealed interface UpdateStatus {
    data object NotChecked : UpdateStatus
    data object Checking : UpdateStatus
    data object UpToDate : UpdateStatus
    data class UpdateAvailable(val release: ReleaseInfo) : UpdateStatus
    data class Error(val message: String) : UpdateStatus
}

