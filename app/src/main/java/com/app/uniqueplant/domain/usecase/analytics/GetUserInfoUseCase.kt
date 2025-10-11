package com.app.uniqueplant.domain.usecase.analytics

import android.util.Log
import com.app.uniqueplant.domain.model.dataModels.Resource
import com.app.uniqueplant.domain.model.dtos.UserInfo
import com.app.uniqueplant.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): UserInfo {
        val emptyInfo = UserInfo(userName = "N/A", email = "N/A", uuid = "")
        val userResource = authRepository.getUserInfo()
        when (userResource) {
            is Resource.Success -> {
                Log.d("GetUserInfoUseCase", "User info fetched successfully: ${userResource.data}")
                return userResource.data ?: emptyInfo
            }

            is Resource.Error -> {
                Log.e("GetUserInfoUseCase", "Error fetching user info: ${userResource.message}")
                return emptyInfo.copy("Error", "Error", "")
            }

            is Resource.Loading -> {
                Log.d("GetUserInfoUseCase", "Loading user info...")
                return emptyInfo
            }
        }
    }
}