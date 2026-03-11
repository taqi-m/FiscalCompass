package com.fiscal.compass.presentation.screens.home.home

import androidx.annotation.DrawableRes


data class FabConfig(
    val primary: FabAction,
    val secondary: FabAction? = null
)

data class FabAction(
    @DrawableRes val iconRes: Int,
    val label: String,
    val contentDescription: String,
    val onClick: () -> Unit
)
