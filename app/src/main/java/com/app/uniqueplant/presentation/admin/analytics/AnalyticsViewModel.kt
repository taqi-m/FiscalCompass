package com.app.uniqueplant.presentation.admin.analytics 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.analytics.GetMonthlyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
) : ViewModel() {
    
    private val _state = MutableStateFlow(AnalyticsScreenState())
    val state: StateFlow<AnalyticsScreenState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)

            val monthlyReport = getMonthlyReportUseCase.invoke(
                month = Calendar.getInstance().get(Calendar.MONTH),
                year = Calendar.getInstance().get(Calendar.YEAR)
            )

            _state.value = _state.value.copy(
                isLoading = false,
                expenses = monthlyReport.expenses,
                incomes = monthlyReport.incomes,
                totalIncomes = monthlyReport.totalIncomes,
                totalExpenses = monthlyReport.totalExpenses,
                totalProfit = monthlyReport.totalProfit
            )
        }
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            // Handle events here
            AnalyticsEvent.LoadAnalytics -> {
                TODO()
            }
        }
    }
    
    private fun updateState(update: AnalyticsScreenState.() -> AnalyticsScreenState) {
        _state.value = _state.value.update()
    }
}