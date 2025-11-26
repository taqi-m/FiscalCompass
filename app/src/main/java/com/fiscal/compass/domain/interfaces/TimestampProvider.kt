package com.fiscal.compass.domain.interfaces

import com.fiscal.compass.data.managers.SyncInfo
import com.fiscal.compass.data.managers.SyncType

interface TimestampProvider {
    fun updateLastSyncTimestamp(syncType: SyncType)
    fun getLastSyncInfo(): SyncInfo
}