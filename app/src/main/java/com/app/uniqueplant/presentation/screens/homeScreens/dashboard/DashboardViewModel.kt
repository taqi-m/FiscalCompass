package com.app.uniqueplant.presentation.screens.homeScreens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.domain.usecase.LoadHomeUserInfo
import com.app.uniqueplant.presentation.navigation.MainScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val loadDefaultsUseCase: LoadDefaultsUseCase,
    private val loadHomeUserInfo: LoadHomeUserInfo
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardScreenState())
    val state: StateFlow<DashboardScreenState> = _state.asStateFlow()

    val coroutineScope = viewModelScope
    private var userCollectionJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                loadUserInfo()
            }
            launch {
                loadDefaultsUseCase.addDefaultCategories()
            }
        }
    }

    private fun loadUserInfo() {
        userCollectionJob?.cancel()
        userCollectionJob = coroutineScope.launch {
            loadHomeUserInfo().collect { userInfo ->
                _state.update { it.copy(userInfo = userInfo) }
            }
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