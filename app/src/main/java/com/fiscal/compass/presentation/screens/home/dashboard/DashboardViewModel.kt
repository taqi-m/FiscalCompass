package com.fiscal.compass.presentation.screens.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.usecase.analytics.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserInfo: GetUserInfoUseCase,
    private val transactionService: TransactionService,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardScreenState())
    val state: StateFlow<DashboardScreenState> = _state.asStateFlow()

    val coroutineScope = viewModelScope
    private var userCollectionJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch { loadUserInfo() }
        }
    }

    private fun loadUserInfo() {
        userCollectionJob?.cancel()
        userCollectionJob = coroutineScope.launch(Dispatchers.IO) {
            val userInfo = runBlocking { getUserInfo() }
            _state.update { it.copy(userInfo = it.userInfo.copy(name = userInfo.userName, profilePictureUrl = userInfo.profilePicUrl)) }
            transactionService.getCurrentMonthBalance().collect { balance ->
                _state.update { it.copy(userInfo = it.userInfo.copy(balance = balance, month = it.userInfo.month)) }
            }
        }
    }

    fun onEvent(event: DashboardEvent) {

    }
}