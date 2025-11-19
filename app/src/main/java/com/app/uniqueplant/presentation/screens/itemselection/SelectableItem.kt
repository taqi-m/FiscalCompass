package com.app.uniqueplant.presentation.screens.itemselection

data class SelectableItem(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
) {
    fun toggle(): SelectableItem = copy(isSelected = !isSelected)
}
