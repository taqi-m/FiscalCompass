package com.fiscal.compass.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf

val LocalDarkTheme = compositionLocalOf { PreferenceUtil.DarkThemePreference() }
val LocalDynamicColorSwitch = compositionLocalOf { false }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    PreferenceUtil.appSettingsStateFlow.collectAsState().value.run {
        CompositionLocalProvider(
            LocalDarkTheme provides darkTheme,
            LocalDynamicColorSwitch provides isDynamicColorEnabled,
            content = content,
        )
    }
}