package com.fiscal.compass.domain.usecase.analytics

import android.util.Log
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.dto.UserInfo
import com.fiscal.compass.domain.repository.AuthRepository
import javax.inject.Inject

class GetOverviewDataUC @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): UserInfo {
        val emptyInfo = UserInfo(userName = "N/A", email = "N/A", uuid = "")
        val userResource = authRepository.getUserInfo()
        when (userResource) {
            is Resource.Success -> {
                Log.d(TAG, "User info fetched successfully: ${userResource.data}")
                return userResource.data ?: emptyInfo
            }

            is Resource.Error -> {
                Log.e(TAG, "Error fetching user info: ${userResource.message}")
                return emptyInfo.copy("Error", "Error", "")
            }

            is Resource.Loading -> {
                Log.d(TAG, "Loading user info...")
                return emptyInfo
            }
        }
    }
    
    companion object {
        private const val TAG = "GetOverviewDataUC"
    }
}