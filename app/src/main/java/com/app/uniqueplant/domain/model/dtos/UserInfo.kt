package com.app.uniqueplant.domain.model.dtos

data class UserInfo(
    val userName: String,
    val email: String,
    val uuid: String,
    val profilePicUrl: String? = null,
)
