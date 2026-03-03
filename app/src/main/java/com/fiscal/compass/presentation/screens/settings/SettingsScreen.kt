package com.fiscal.compass.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiscal.compass.BuildConfig
import com.fiscal.compass.domain.model.update.UpdateStatus
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.screens.settings.update.UpdateDialog
import com.fiscal.compass.ui.components.ThemeSwitch
import com.fiscal.compass.ui.components.cards.ProfileCard
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import com.fiscal.compass.ui.util.PreferenceUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    onEvent: (SettingsEvent) -> Unit,
    appNavController: NavHostController,
    onLogout: (String) -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(state.isLogOutSuccess) {
        if (state.isLogOutSuccess) {
            onLogout(MainScreens.Auth.route)
        }
    }

    // Show toast when up to date
    LaunchedEffect(state.updateStatus) {
        if (state.updateStatus is UpdateStatus.UpToDate) {
            Toast.makeText(context, "You're on the latest version", Toast.LENGTH_SHORT).show()
        }
        if (state.updateStatus is UpdateStatus.Error) {
            Toast.makeText(
                context,
                (state.updateStatus as UpdateStatus.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Update available dialog
    if (state.updateStatus is UpdateStatus.UpdateAvailable) {
        UpdateDialog(
            release = (state.updateStatus as UpdateStatus.UpdateAvailable).release,
            downloadProgress = state.downloadProgress,
            apkDownloaded = state.apkDownloaded,
            onDownload = onDownloadUpdate,
            onInstall = onInstallUpdate,
            onDismiss = { onEvent(SettingsEvent.DismissUpdateDialog) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings", style = MaterialTheme.typography.titleLarge
                    )
                }, colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ), navigationIcon = {
                    IconButton(
                        onClick = {
                            appNavController.popBackStack()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            ProfileCard(
                modifier = Modifier.fillMaxWidth(),
                name = state.userInfo?.userName,
                email = state.userInfo?.userEmail,
                onClick = {
                    onEvent(SettingsEvent.OnLogoutClicked)
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            ThemeSwitch(
                modifier = Modifier.fillMaxWidth(), onSwitchChange = { themeMode ->
                    PreferenceUtil.modifyDarkThemePreference(
                        darkThemeValue = themeMode as Int,
                    )
                })

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            // Check for updates row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = state.updateStatus !is UpdateStatus.Checking) {
                        onEvent(SettingsEvent.CheckForUpdate)
                    }
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Check for updates",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Current version: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (state.updateStatus is UpdateStatus.Checking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()

    FiscalCompassTheme {
        SettingsScreen(
            state = SettingsScreenState(
                userInfo = UserInfo(
                    userName = "John Doe",
                    userEmail = "john.doe@example.com",
                    profilePictureUrl = "https://miro.medium.com/v2/resize:fit:640/format:webp/1*e8M-qkVP2y0dK4waDxGmbw.png"
                ),
                isLoading = true,
            ),
            onEvent = {},
            appNavController = navController,
            onLogout = {},
            onDownloadUpdate = {},
            onInstallUpdate = {}
        )
    }
}