package com.app.uniqueplant.domain.usecase.analytics

import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.repository.AuthRepository
import com.app.uniqueplant.presentation.screens.homeScreens.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

//FIXME: This use case is getting too big, consider breaking it down if it grows more
class LoadHomeUserInfoUC @Inject constructor(
    private val authRepository: AuthRepository,
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase
) {
    suspend operator fun invoke(): Flow<UserInfo> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val user = authRepository.getUserInfo().first()
        val userName = (user as? Resource.Success)?.data?.userName ?: "User"

        val report = getMonthlyReportUseCase(currentMonth , currentYear)
        return report.combine(emptyFlow<UserInfo>()) { monthlyReport, _ ->
            UserInfo(userName, monthlyReport.totalProfit)
        }
    }
}