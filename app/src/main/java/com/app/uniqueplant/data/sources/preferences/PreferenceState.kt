package com.app.uniqueplant.data.sources.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class PreferenceState<T>(
    private val key: Preferences.Key<T>,
    private val defaultValue: T,
    private val dataStore: DataStore<Preferences>
) : MutableState<T> {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _state = mutableStateOf(defaultValue)

    override var value: T
        get() = _state.value
        set(value) {
            _state.value = value
            scope.launch { dataStore.edit { it[key] = value } } // Auto-save
        }

    override fun component1(): T = value

    override fun component2(): (T) -> Unit = { value = it }

    init {
        scope.launch {
            dataStore.data
                .map { it[key] ?: defaultValue }
                .collect { _state.value = it }
        }
    }
}