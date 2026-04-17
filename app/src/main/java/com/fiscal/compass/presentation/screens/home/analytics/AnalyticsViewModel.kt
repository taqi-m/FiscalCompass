package com.fiscal.compass.presentation.screens.home.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.usecase.analytics.GetMonthlyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsScreenState())
    val state: StateFlow<AnalyticsScreenState> = _state.asStateFlow()

    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var loadJob: Job? = null

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.LoadAnalytics -> {
                loadAnalyticsData(month = currentMonth, year = currentYear)
            }

            is AnalyticsEvent.LoadAnalyticsForPeriod -> {
                loadAnalyticsData(event.month, event.year)
            }
        }
    }

    private fun loadAnalyticsData(month: Int, year: Int) {
        val isSamePeriod = _state.value.selectedMonth == month && _state.value.selectedYear == year
        if (_state.value.displayState != null && isSamePeriod) {
            return
        }

        updateState {
            copy(
                displayState = AnalyticsDisplayState.Loading,
                selectedMonth = month,
                selectedYear = year,
            )
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            getMonthlyReportUseCase(month, year)
                .onEach { report ->
                    updateState {
                        copy(
                            displayState = AnalyticsDisplayState.Content(
                                expenses = report.expenses,
                                incomes = report.incomes,
                                totalIncomes = report.totalIncomes,
                                totalExpenses = report.totalExpenses,
                                totalProfit = report.totalProfit,
                            ),
                        )
                    }
                }
                .catch { exception ->
                    updateState {
                        copy(
                            displayState = AnalyticsDisplayState.Error(
                                message = exception.message ?: "An unknown error occurred",
                            ),
                        )
                    }
                }
                .collect {}
        }
    }

    private fun updateState(update: AnalyticsScreenState.() -> AnalyticsScreenState) {
        _state.update { it.update() }
    }
}