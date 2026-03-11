package com.fiscal.compass.presentation.screens.home.home

import com.fiscal.compass.presentation.screens.category.UiState

data class HomeScreenState(
    val uiState: UiState = UiState.Idle,
    val canViewCategories: Boolean = false,
    val canViewPeople: Boolean = false,
    val canAddPerson: Boolean = false,
    val canManageUsers: Boolean = false,
)