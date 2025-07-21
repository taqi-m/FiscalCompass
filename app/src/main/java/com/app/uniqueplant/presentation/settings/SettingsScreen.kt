package com.app.uniqueplant.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.navigation.MainScreens
import com.app.uniqueplant.ui.components.ThemeSwitch
import com.app.uniqueplant.ui.components.cards.ProfileCard
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    onEvent: (SettingsEvent) -> Unit,
    onLogout: (String) -> Unit,
) {
    LaunchedEffect(
        state.isLogOutSuccess
    ) {
        if (state.isLogOutSuccess) {
            onLogout(MainScreens.Auth.route)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileCard(
            modifier = Modifier.fillMaxWidth(),
            name = state.userInfo?.userName,
            email = state.userInfo?.userEmail,
            onClick = {
                onEvent(SettingsEvent.OnLogoutClicked)
            },
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        ThemeSwitch(
            modifier = Modifier.fillMaxWidth(),
            onSwitchChange = { isChecked ->
                onEvent(SettingsEvent.OnThemeSwitchChanged(isChecked))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    UniquePlantTheme {
        SettingsScreen(
            state = SettingsScreenState(
                userInfo = UserInfo(
                    userName = "John Doe",
                    userEmail = "john.doe@example.com",
                    profilePictureUrl = "https://miro.medium.com/v2/resize:fit:640/format:webp/1*e8M-qkVP2y0dK4waDxGmbw.png"
                )
            ),
            onEvent = {

            },
            onLogout = { route ->
                // Handle logout action
            },
        )
    }
}