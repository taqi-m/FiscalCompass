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
    private var recentTransactionsJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch { loadUserInfo() }
            launch { loadRecentTransactions() }
        }
    }

    private fun loadUserInfo() {
        userCollectionJob?.cancel()
        userCollectionJob = coroutineScope.launch(Dispatchers.IO) {
            val userInfo = getUserInfo()
            _state.update { it.copy(userInfo = it.userInfo.copy(name = userInfo.userName, profilePictureUrl = userInfo.profilePicUrl)) }
            transactionService.getCurrentMonthBalance().collect { balance ->
                _state.update { it.copy(userInfo = it.userInfo.copy(balance = balance, month = it.userInfo.month)) }
            }
        }
    }

    private fun loadRecentTransactions() {
        recentTransactionsJob?.cancel()
        recentTransactionsJob = coroutineScope.launch(Dispatchers.IO) {
            transactionService.loadCurrentMonthTransactions().collect { groupedTransactions ->
                val recentTransactions = groupedTransactions
                    .values
                    .flatten()
                    .sortedByDescending { it.date }
                    .take(3)

                _state.update { it.copy(recentTransactions = recentTransactions) }
            }
        }
    }

    fun onEvent(event: DashboardEvent) {
        if (event != DashboardEvent.OnScreenLoad) return

        loadUserInfo()
        loadRecentTransactions()
    }
}