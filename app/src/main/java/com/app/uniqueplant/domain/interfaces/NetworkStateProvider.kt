package com.app.uniqueplant.domain.interfaces

import kotlinx.coroutines.flow.StateFlow

interface NetworkStateProvider {
    val networkStateFlow: StateFlow<Boolean>
    fun isOnline(): Boolean
}