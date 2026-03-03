package com.fiscal.compass.presentation.screens.person

import com.fiscal.compass.domain.model.base.Person

/**
 * Sealed interface representing all possible navigation destinations from PersonScreen.
 * This allows for type-safe navigation without passing NavHostController directly to the screen.
 */
sealed interface PersonNavigation {
    /**
     * Navigate back to the previous screen
     */
    data object NavigateBack : PersonNavigation

    /**
     * Navigate to add person screen
     * @param selectedType The currently selected person type to pre-select in the form
     */
    data class NavigateToAddPerson(
        val selectedType: String
    ) : PersonNavigation

    /**
     * Navigate to edit person screen
     * @param person Person object to edit
     */
    data class NavigateToEditPerson(
        val person: Person
    ) : PersonNavigation
}


