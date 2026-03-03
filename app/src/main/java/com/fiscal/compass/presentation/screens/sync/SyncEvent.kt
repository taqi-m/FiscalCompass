package com.fiscal.compass.presentation.screens.sync

sealed class SyncEvent {
    object ForceSync : SyncEvent()
}