package com.fiscal.compass.domain.model.dto

data class UserInfo(
    val userName: String,
    val email: String,
    val uuid: String,
    val profilePicUrl: String? = null,
)
