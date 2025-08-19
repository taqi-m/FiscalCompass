package com.app.uniqueplant.domain.model

data class Person(
    val personId: Long = 0,
    val name: String,
    val personType: String,
    val contact: String? = null,
)