package com.fiscal.compass.data.remote.update

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the GitHub Releases API response.
 * Only the fields we need are declared; unknown keys are ignored by default.
 */
@Serializable
data class GitHubReleaseDto(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String,
    @SerialName("body") val body: String = "",
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("assets") val assets: List<GitHubAssetDto> = emptyList()
)

@Serializable
data class GitHubAssetDto(
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    @SerialName("name") val name: String,
    @SerialName("content_type") val contentType: String = ""
)

