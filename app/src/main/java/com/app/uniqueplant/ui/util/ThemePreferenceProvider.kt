package com.app.uniqueplant.ui.util

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalThemeMode = staticCompositionLocalOf { ThemeMode.FOLLOW_SYSTEM }

@Composable
fun ThemePreferenceProvider(
    viewModel: ThemePreferenceViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    CompositionLocalProvider(LocalThemeMode provides themeMode, content = content)
}