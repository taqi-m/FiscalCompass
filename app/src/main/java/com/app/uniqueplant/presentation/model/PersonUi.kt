package com.app.uniqueplant.presentation.model

data class PersonUi(
    val personId: Long = 0,
    val name: String = "",
    val personType: String,
    val contact: String? = null,
)
