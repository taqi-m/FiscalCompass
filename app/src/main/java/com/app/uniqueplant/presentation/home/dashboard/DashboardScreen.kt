package com.app.uniqueplant.presentation.home.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardEvent) -> Unit,
    onLogout: () -> Unit
) {
    // This is where the dashboard UI will be implemented
    // For now, we can just display a simple text or placeholder
    // You can use MaterialTheme, Scaffold, etc. to build the UI
    // Example:

    val userType = state.userTypeName.ifEmpty { "User Type not Fetched" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Dashboard Screen")
            Spacer(
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "User Type: $userType",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    onEvent(DashboardEvent.LogoutClicked)
                    onLogout()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Logout")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        state = DashboardScreenState(userTypeName = "User"),
        onEvent = {},
        onLogout = {}
    )
}