package com.fiscal.compass.domain.usecase.person

import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.presentation.screens.category.UiState
import javax.inject.Inject

class DeletePersonUseCase @Inject constructor(
    private val personDao: PersonDao
) {
    suspend operator fun invoke(person: Person): UiState {
        try {
            personDao.delete(person.toEntity())
            return UiState.Success("Person deleted successfully.")

        } catch (e: Exception) {
            return UiState.Error(e.message ?: "An error occurred while deleting the person.")
        }
    }
}