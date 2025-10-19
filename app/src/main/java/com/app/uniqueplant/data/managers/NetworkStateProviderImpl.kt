package com.app.uniqueplant.data.managers

import com.app.uniqueplant.domain.interfaces.NetworkStateProvider
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NetworkStateProviderImpl @Inject constructor(
    private val networkManager: NetworkManager
) : NetworkStateProvider {
    override val networkStateFlow: StateFlow<Boolean>
        get() = networkManager.networkStateFlow

    override fun isOnline(): Boolean {
        return networkManager.isOnline()
    }
}