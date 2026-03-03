package com.fiscal.compass.data.remote.update

import com.fiscal.compass.BuildConfig
import com.fiscal.compass.domain.interfaces.UpdateSource
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.update.ReleaseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * Fetches the latest release from the GitHub Releases API.
 *
 * - 404 is treated as "no releases published yet" → returns a null-data success.
 * - The first `.apk` asset is used as the download URL.
 */
class GitHubUpdateSource @Inject constructor(
    private val okHttpClient: OkHttpClient
) : UpdateSource {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getLatestRelease(): Resource<ReleaseInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.github.com/repos/" +
                    "${BuildConfig.GITHUB_REPO_OWNER}/${BuildConfig.GITHUB_REPO_NAME}/releases/latest"

            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            when (response.code) {
                200 -> {
                    val body = response.body?.string()
                        ?: return@withContext Resource.Error("Empty response body")

                    val dto = json.decodeFromString<GitHubReleaseDto>(body)

                    val apkAsset = dto.assets.firstOrNull { it.name.endsWith(".apk") }

                    val releaseInfo = ReleaseInfo(
                        tagName = dto.tagName,
                        name = dto.name,
                        body = dto.body,
                        downloadUrl = apkAsset?.browserDownloadUrl ?: "",
                        htmlUrl = dto.htmlUrl
                    )
                    Resource.Success(releaseInfo)
                }

                404 -> {
                    // No releases published yet
                    Resource.Success(null)
                }

                else -> {
                    Resource.Error("GitHub API error: ${response.code} ${response.message}")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error checking for updates")
        }
    }
}


