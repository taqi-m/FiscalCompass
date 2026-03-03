package com.fiscal.compass.presentation.screens.sync

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.ui.theme.FiscalCompassTheme

// ─────────────────────────────────────────────────────────────────────────────
// Screen entry point
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    state: SyncScreenState,
    onEvent: (SyncEvent) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sync Data") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back_24),
                            contentDescription = "Back",
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SyncScreenContent(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onEvent = onEvent
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncScreenContent(
    modifier: Modifier = Modifier,
    state: SyncScreenState,
    onEvent: (SyncEvent) -> Unit
) {
    val hasPending = state.pendingExpenses > 0 || state.pendingIncomes > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Top spacer so the central section sits in the upper-centre ──
        Spacer(modifier = Modifier.height(32.dp))

        // ── Central hero section ──
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Cloud icon with optional syncing overlay
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isSyncing) R.drawable.ic_cloud_sync_24
                            else R.drawable.ic_cloud_done_96
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status title
            Text(
                text = when {
                    state.isSyncing -> "Syncing…"
                    hasPending      -> "Pending Upload"
                    else            -> "Up to Date"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Last synced subtitle
            Text(
                text = if (state.lastSyncTime != null)
                    "Last synced: ${formatRelativeTime(state.lastSyncTime)}"
                else
                    "Never synced",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // Pending count chips
            AnimatedVisibility(visible = hasPending && !state.isSyncing) {
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.pendingExpenses > 0) {
                        AssistChip(
                            onClick = {},
                            label = { Text("↑ ${state.pendingExpenses} expense${if (state.pendingExpenses > 1) "s" else ""}") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                    if (state.pendingIncomes > 0) {
                        AssistChip(
                            onClick = {},
                            label = { Text("↑ ${state.pendingIncomes} income${if (state.pendingIncomes > 1) "s" else ""}") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar — only while syncing
            AnimatedVisibility(
                visible = state.isSyncing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        // ── Bottom banners + action ──
        Column(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // No internet banner
            AnimatedVisibility(visible = !state.isOnline) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_error_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "No internet connection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Error banner
            AnimatedVisibility(visible = state.errorMessage != null && !state.isSyncing) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_error_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = state.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Force Sync secondary text button
            TextButton(
                onClick = { onEvent(SyncEvent.ForceSync) },
                enabled = !state.isSyncing && state.isOnline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Force Full Sync",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatRelativeTime(epochMs: Long): String {
    val diff = System.currentTimeMillis() - epochMs
    val minutes = diff / 60_000
    val hours = diff / 3_600_000
    return when {
        diff < 60_000              -> "Just now"
        minutes < 60              -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        hours < 24                -> "$hours hour${if (hours > 1) "s" else ""} ago"
        else                      -> DateTimeUtil.formatTimestamp(epochMs)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(
    name = "Synced – Light",
    showBackground = true
)
@Composable
private fun PreviewSynced() {
    FiscalCompassTheme {
        SyncScreen(
            state = SyncScreenState(
                isOnline = true,
                lastSyncTime = System.currentTimeMillis() - 7_200_000L
            ),
            onEvent = {}
        )
    }
}

@Preview(
    name = "Pending – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PreviewPending() {
    FiscalCompassTheme {
        SyncScreen(
            state = SyncScreenState(
                isOnline = true,
                pendingExpenses = 5,
                pendingIncomes = 2,
                lastSyncTime = System.currentTimeMillis() - 3_600_000L
            ),
            onEvent = {}
        )
    }
}

@Preview(
    name = "Syncing – Light",
    showBackground = true
)
@Composable
private fun PreviewSyncing() {
    FiscalCompassTheme {
        SyncScreen(
            state = SyncScreenState(isSyncing = true, isOnline = true),
            onEvent = {}
        )
    }
}

@Preview(
    name = "Offline + Error – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PreviewOfflineError() {
    FiscalCompassTheme {
        SyncScreen(
            state = SyncScreenState(
                isOnline = false,
                errorMessage = "Sync failed: timeout",
                pendingExpenses = 3
            ),
            onEvent = {}
        )
    }
}
