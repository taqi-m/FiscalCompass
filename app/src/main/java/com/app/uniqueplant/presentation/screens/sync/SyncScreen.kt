package com.app.uniqueplant.presentation.screens.sync

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    state: SyncScreenState,
    onEvent: (SyncEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sync Data") },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Handle back navigation */ }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back_24),
                            contentDescription = "Back",
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            )
        },
    ) {
        SyncScreenContent(modifier = Modifier.padding(it), state = state, onEvent = onEvent)
    }
}

@Composable
fun SyncScreenContent(
    modifier: Modifier = Modifier,
    state: SyncScreenState,
    onEvent: (SyncEvent) -> Unit
) {

    val hasUnsyncedData = state.hasUnsyncedData
    val isConnected = state.isConnected.collectAsState().value
    val syncStatus: String = remember(state.hasUnsyncedData) {
        if (hasUnsyncedData) {
            "You have unsynced data."
        } else {
            "All data is synced."
        }
    }

    val connectedStatus: String = remember(state.isConnected.collectAsState().value) {
        if (isConnected) {
            "Online"
        } else {
            "Offline"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal =  16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .padding(8.dp),
            text = syncStatus,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        HorizontalDivider()

        TextRow(
            label = "Unsynced Expenses:",
            value = state.unsyncedExpenseCount.toString()
        )
        TextRow(
            label = "Unsynced Incomes:",
            value = state.unsyncedIncomeCount.toString()
        )

        HorizontalDivider()

        TextRow(
            label = "Connectivity Status:",
            value = connectedStatus
        )

        HorizontalDivider()

        Button(
            onClick = {
                        onEvent(SyncEvent.SyncAll)
            },
            enabled = hasUnsyncedData && isConnected,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = "Sync Now")
        }
    }

}

@Composable
private fun TextRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(
    showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SyncScreenPreview() {
    val state = SyncScreenState()
    UniquePlantTheme { SyncScreen(state = state, onEvent = {}) }
}
