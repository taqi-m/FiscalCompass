package com.fiscal.compass.presentation.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.BuildConfig
import com.fiscal.compass.domain.interfaces.UpdateSource
import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.update.UpdateStatus
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.usecase.analytics.GetOverviewDataUC
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.util.VersionComparator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getOverviewDataUC: GetOverviewDataUC,
    private val sessionUseCase: SessionUseCase,
    private val analyticsService: AnalyticsService,
    private val updateSource: UpdateSource,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            getOverviewDataUC().let { userInfo ->
                val userInfo = UserInfo(
                    userName = userInfo.userName,
                    userEmail = userInfo.email,
                    profilePictureUrl = userInfo.profilePicUrl
                )
                _state.value = _state.value.copy(userInfo = userInfo, isLoading = false)
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClicked -> logout()
            is SettingsEvent.CheckForUpdate -> checkForUpdate()
            is SettingsEvent.DownloadUpdate -> { /* triggered via downloadUpdate(context) from UI */ }
            is SettingsEvent.InstallUpdate -> { /* triggered via installApk(context) from UI */ }
            is SettingsEvent.DismissUpdateDialog -> dismissUpdateDialog()
        }
    }

    private fun checkForUpdate() {
        updateState { copy(updateStatus = UpdateStatus.Checking) }

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = updateSource.getLatestRelease()) {
                is Resource.Success -> {
                    val release = result.data
                    if (release == null) {
                        // No releases published yet
                        updateState { copy(updateStatus = UpdateStatus.UpToDate) }
                        return@launch
                    }

                    val currentVersion = BuildConfig.VERSION_NAME
                    if (VersionComparator.isNewer(currentVersion, release.tagName)) {
                        updateState { copy(updateStatus = UpdateStatus.UpdateAvailable(release)) }
                    } else {
                        updateState { copy(updateStatus = UpdateStatus.UpToDate) }
                    }
                }

                is Resource.Error -> {
                    updateState {
                        copy(updateStatus = UpdateStatus.Error(
                            result.message ?: "Failed to check for updates"
                        ))
                    }
                }

                is Resource.Loading -> {
                    updateState { copy(updateStatus = UpdateStatus.Checking) }
                }
            }
        }
    }

    fun downloadUpdate(context: Context) {
        val status = _state.value.updateStatus
        if (status !is UpdateStatus.UpdateAvailable) return
        val downloadUrl = status.release.downloadUrl
        if (downloadUrl.isBlank()) {
            updateState { copy(updateStatus = UpdateStatus.Error("No APK available for download")) }
            return
        }

        updateState { copy(downloadProgress = 0f, apkDownloaded = false) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(downloadUrl).build()
                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    updateState { copy(updateStatus = UpdateStatus.Error("Download failed: ${response.code}")) }
                    return@launch
                }

                val body = response.body ?: run {
                    updateState { copy(updateStatus = UpdateStatus.Error("Empty download response")) }
                    return@launch
                }

                val totalBytes = body.contentLength()
                var bytesRead = 0L

                val updatesDir = File(context.cacheDir, "updates")
                if (!updatesDir.exists()) updatesDir.mkdirs()
                val apkFile = File(updatesDir, "update.apk")

                apkFile.outputStream().use { output ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(8192)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            bytesRead += read
                            if (totalBytes > 0) {
                                val progress = bytesRead.toFloat() / totalBytes.toFloat()
                                updateState { copy(downloadProgress = progress) }
                            }
                        }
                    }
                }

                updateState { copy(downloadProgress = 1f, apkDownloaded = true) }

            } catch (e: Exception) {
                updateState {
                    copy(updateStatus = UpdateStatus.Error(
                        e.message ?: "Download failed"
                    ))
                }
            }
        }
    }

    fun installApk(context: Context) {
        try {
            val updatesDir = File(context.cacheDir, "updates")
            val apkFile = File(updatesDir, "update.apk")
            if (!apkFile.exists()) return

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            updateState {
                copy(updateStatus = UpdateStatus.Error(
                    e.message ?: "Failed to install update"
                ))
            }
        }
    }

    private fun dismissUpdateDialog() {
        updateState {
            copy(
                updateStatus = UpdateStatus.NotChecked,
                downloadProgress = 0f,
                apkDownloaded = false
            )
        }
    }

    private fun logout() {
        analyticsService.logEvent(AnalyticsEvent.Logout)
        analyticsService.setUserId(null)
        viewModelScope.launch(Dispatchers.IO) {
            sessionUseCase.logout().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        updateState {
                            copy(
                                userInfo = null,
                                error = null,
                                isLogOutSuccess = true
                            )
                        }
                    }

                    is Resource.Error -> {
                        updateState { copy(error = resource.message) }
                    }

                    is Resource.Loading -> {
                        updateState { copy(isLoading = true) }
                    }
                }
            }
        }
    }


    private fun updateState(update: SettingsScreenState.() -> SettingsScreenState) {
        _state.value = _state.value.update()
    }
}