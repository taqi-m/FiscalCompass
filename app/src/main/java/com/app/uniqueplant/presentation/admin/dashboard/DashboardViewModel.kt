package com.app.uniqueplant.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.presentation.navigation.MainScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val loadDefaultsUseCase: LoadDefaultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardScreenState())
    val state: StateFlow<DashboardScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                loadDefaultsUseCase.addDefaultCategories()
            }.join()
        }
    }


    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.AddExpenseClicked -> {

            }

            is DashboardEvent.OnScreenLoad -> {
                _state.value = _state.value.copy(appNavController = event.appNavController)
            }

            is DashboardEvent.OnAddTransactionClicked -> {
                val appNavController = _state.value.appNavController
                appNavController?.navigate(MainScreens.AddTransaction.route)
            }

            is DashboardEvent.OnCategoriesClicked -> {
                val appNavController = _state.value.appNavController
                appNavController?.navigate(MainScreens.Categories.route)
            }

            is DashboardEvent.OnPersonsClicked -> {
                val appNavController = _state.value.appNavController
                appNavController?.navigate(MainScreens.Person.route)
            }

            is DashboardEvent.OnJobsClicked -> {
                val appNavController = _state.value.appNavController
                appNavController?.navigate(MainScreens.Jobs.route)
            }
        }
    }
}