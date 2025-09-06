package com.app.uniqueplant.domain.usecase

import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.repository.AuthRepository
import com.app.uniqueplant.domain.usecase.analytics.GetMonthlyReportUseCase
import com.app.uniqueplant.presentation.screens.homeScreens.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class LoadHomeUserInfo @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<UserInfo> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return combine(
            authRepository.getUserInfo(),
            getMonthlyReportUseCase.getNetAmount(currentMonth,currentYear)
        ) { userResource, reportResource ->
            val name = when (userResource) {
                is Resource.Success -> userResource.data?.userName ?: "User"
                else -> "User"
            }
            UserInfo(
                name = name,
                balance = reportResource,
            )
        }
    }
}