package com.app.uniqueplant.domain.interfaces

import com.app.uniqueplant.data.managers.SyncInfo
import com.app.uniqueplant.data.managers.SyncType

interface TimestampProvider {
    fun updateLastSyncTimestamp(syncType: SyncType)
    fun getLastSyncInfo(): SyncInfo
}