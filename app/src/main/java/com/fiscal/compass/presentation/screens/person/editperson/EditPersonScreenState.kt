package com.fiscal.compass.presentation.screens.person.editperson

data class EditPersonScreenState(
    val personId: String = "",
    val name: String = "",
    val contact: String = "",
    val selectedType: String = "",
    val availableTypes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

