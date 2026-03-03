package com.fiscal.compass.domain.interfaces

import com.fiscal.compass.domain.model.sync.SyncInfo
import com.fiscal.compass.domain.model.sync.SyncType

interface TimestampProvider {
    fun updateLastSyncTimestamp(syncType: SyncType)
    fun getLastSyncInfo(): SyncInfo
    fun resetTimestamps()
}