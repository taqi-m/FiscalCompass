package com.app.uniqueplant.data.managers

import com.app.uniqueplant.domain.interfaces.TimestampProvider

import javax.inject.Inject

class SyncTimestampManagerImpl @Inject constructor(
    private val syncTimestampManager: SyncTimestampManager
) : TimestampProvider {
    override fun updateLastSyncTimestamp(syncType: SyncType) {
        syncTimestampManager.updateLastSyncTimestamp(syncType)
    }

    override fun getLastSyncInfo(): SyncInfo {
        return syncTimestampManager.getLastSyncInfo()
    }
}