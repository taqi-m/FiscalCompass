package com.app.uniqueplant.presentation.screens.sync

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.components.cards.SplitIconCard
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
        SyncScreenContent(modifier = Modifier.padding(it))
    }
}

@Composable
fun SyncScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SplitIconCard(
            title = "Sync All Data",
            description = "Sync all your data to the cloud.",
            icon = {
                SyncedIcon()
            },
            onClick = { /* Handle sync all action */ }
        )

        SplitIconCard(
            title = "Expenses",
            description = "Sync your expenses data.",
            icon = {
                SyncedIcon()
            },
            onClick = { /* Handle cancel sync action */ }
        )

        SplitIconCard(
            title = "Incomes",
            description = "Sync your income data.",
            icon = {
                SyncedIcon()
            },
            onClick = { /* Handle cancel sync action */ }
        )

        SplitIconCard(
            title = "Categories",
            description = "Sync your categories data.",
            icon = {
                SyncedIcon()
            },
            onClick = { /* Handle cancel sync action */ }
        )
    }
}

@Composable
private fun SyncedIcon() {
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        painter = painterResource(R.drawable.ic_cloud_done_96),
        contentDescription = "Synced Icon",
        tint = MaterialTheme.colorScheme.primary
    )
}

@Preview(
    showSystemUi = false, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SyncScreenPreview() {
    val state = SyncScreenState(isLoading = false, error = null)
    UniquePlantTheme { SyncScreen(state = state, onEvent = {}) }
}
