package com.fiscal.compass.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

// Animation duration constants for consistent transitions across all nav graphs
internal const val NAV_TRANSITION_DURATION = 200
internal const val NAV_FADE_TRANSITION_DURATION = (NAV_TRANSITION_DURATION * 1.5).toInt()

internal val navFadeIn = fadeIn(animationSpec = tween(NAV_FADE_TRANSITION_DURATION))
internal val navFadeOut = fadeOut(animationSpec = tween(NAV_FADE_TRANSITION_DURATION))

internal val navEnterFromLeft =
    fadeIn(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideInHorizontally(animationSpec = tween(NAV_TRANSITION_DURATION)) { -it }

internal val navEnterFromRight =
    fadeIn(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideInHorizontally(animationSpec = tween(NAV_TRANSITION_DURATION)) { it }

internal val navEnterFromUp =
    fadeIn(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideInVertically(animationSpec = tween(NAV_TRANSITION_DURATION)) { it }

internal val navExitToLeft =
    fadeOut(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideOutHorizontally(animationSpec = tween(NAV_TRANSITION_DURATION)) { -it }

internal val navExitToRight =
    fadeOut(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideOutHorizontally(animationSpec = tween(NAV_TRANSITION_DURATION)) { it }

internal val navExitToDown =
    fadeOut(animationSpec = tween(NAV_FADE_TRANSITION_DURATION)) +
            slideOutVertically(animationSpec = tween(NAV_TRANSITION_DURATION)) { it }

