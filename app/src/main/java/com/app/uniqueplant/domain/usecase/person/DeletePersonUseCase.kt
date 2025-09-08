package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.mapper.toEntity
import com.app.uniqueplant.data.sources.local.dao.PersonDao
import com.app.uniqueplant.domain.model.Person
import com.app.uniqueplant.presentation.screens.categories.UiState
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