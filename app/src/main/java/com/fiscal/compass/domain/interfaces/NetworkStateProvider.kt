package com.fiscal.compass.domain.interfaces

import kotlinx.coroutines.flow.StateFlow

interface NetworkStateProvider {
    val networkStateFlow: StateFlow<Boolean>
    fun isOnline(): Boolean
}