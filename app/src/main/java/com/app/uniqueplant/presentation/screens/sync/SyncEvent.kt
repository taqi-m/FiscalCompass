package com.app.uniqueplant.presentation.screens.sync

sealed class SyncEvent {
    object SyncAll: SyncEvent()
    object CancelSync: SyncEvent()

}