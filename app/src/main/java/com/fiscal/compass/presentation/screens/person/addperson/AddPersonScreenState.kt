package com.fiscal.compass.presentation.screens.person.addperson

data class AddPersonScreenState(
    val name: String = "",
    val contact: String = "",
    val selectedType: String = "",
    val availableTypes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

