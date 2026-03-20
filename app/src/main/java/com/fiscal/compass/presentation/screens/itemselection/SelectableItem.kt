package com.fiscal.compass.presentation.screens.itemselection

data class SelectableItem(
    val id: String,
    val name: String,
    val description: String? = null,
    val isSelected: Boolean = false
) {
    fun toggle(): SelectableItem = copy(isSelected = !isSelected)
}
