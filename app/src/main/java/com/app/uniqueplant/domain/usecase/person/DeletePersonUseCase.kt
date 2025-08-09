package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.datasource.local.dao.PersonDao
import com.app.uniqueplant.data.model.Person
import com.app.uniqueplant.presentation.admin.categories.UiState

class DeletePersonUseCase(
    private val personDao: PersonDao
) {
    suspend operator fun invoke(person: Person): UiState {
        try {
            personDao.delete(person)
            return UiState.Success("Person deleted successfully.")

        } catch (e: Exception) {
            return UiState.Error(e.message ?: "An error occurred while deleting the person.")
        }
    }
}