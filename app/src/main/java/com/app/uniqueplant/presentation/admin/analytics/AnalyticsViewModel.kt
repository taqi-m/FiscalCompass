package com.app.uniqueplant.presentation.admin.analytics 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.analytics.GetMonthlyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    init {
        loadAnalyticsData(currentMonth, currentYear)
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.LoadAnalytics -> {
                loadAnalyticsData(event.month, event.year)
            }
        }
    }

    private fun loadAnalyticsData(month: Int, year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, error = null)
            getMonthlyReportUseCase(month, year)
                .onEach { report ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        expenses = report.expenses,
                        incomes = report.incomes,
                        totalIncomes = report.totalIncomes,
                        totalExpenses = report.totalExpenses,
                        totalProfit = report.totalProfit
                    )
                }
                .catch { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "An unknown error occurred"
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    private fun updateState(update: AnalyticsScreenState.() -> AnalyticsScreenState) {
        _state.value = _state.value.update()
    }
}