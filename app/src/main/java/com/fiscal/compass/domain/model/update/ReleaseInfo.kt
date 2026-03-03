package com.fiscal.compass.domain.model.update

/**
 * Domain model representing a release from any distribution source.
 * This is source-agnostic — GitHub, Firebase Remote Config, or any
 * future provider maps their response into this model.
 */
data class ReleaseInfo(
    val tagName: String,
    val name: String,
    val body: String,
    val downloadUrl: String,
    val htmlUrl: String
)

