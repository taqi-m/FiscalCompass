package com.fiscal.compass.presentation.screens.sync

sealed class SyncEvent {
    object SyncAll: SyncEvent()
    object CancelSync: SyncEvent()

}