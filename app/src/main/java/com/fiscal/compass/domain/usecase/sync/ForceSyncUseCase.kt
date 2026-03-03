package com.fiscal.compass.domain.usecase.sync

import com.fiscal.compass.domain.sync.AutoSyncManager
import javax.inject.Inject

class ForceSyncUseCase @Inject constructor(
    private val autoSyncManager: AutoSyncManager
) {
    suspend operator fun invoke() = autoSyncManager.forceSyncAll()
}

