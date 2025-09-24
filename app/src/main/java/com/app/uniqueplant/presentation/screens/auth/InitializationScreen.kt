package com.app.uniqueplant.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.data.manager.InitializationStatus

@Composable
fun InitializationScreen(
    initializationStatus: InitializationStatus,
    onRetry: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit
) {
    LaunchedEffect(initializationStatus.isCompleted) {
        if (initializationStatus.isCompleted) {
            onComplete()
        }
    }

    when {
        initializationStatus.error != null -> {
            InitializationErrorScreen(
                error = initializationStatus.error,
                onRetry = onRetry,
                onSkip = onSkip
            )
        }

        initializationStatus.isInitializing -> {
            InitializationProgressScreen(status = initializationStatus)
        }

        else -> {
            // Initial state - could show a splash or start initialization
            LaunchedEffect(Unit) {
                // Initialization should be triggered from AuthViewModel
            }
        }
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun InitializationScreenPreview() {
    InitializationScreen(
        initializationStatus = InitializationStatus(
            isInitializing = true,
            currentStep = "Initializing categories...",
            progress = 0.25f
        ),
        onRetry = {},
        onSkip = {},
        onComplete = {}
    )
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun InitializationScreenErrorPreview() {
    InitializationScreen(
        initializationStatus = InitializationStatus(error = "Network connection failed"),
        onRetry = {},
        onSkip = {},
        onComplete = {}
    )
}

@Composable
fun InitializationProgressScreen(status: InitializationStatus) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Setting up your app...",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        LinearProgressIndicator(
        progress = { status.progress },
        modifier = Modifier.fillMaxWidth(),
        color = ProgressIndicatorDefaults.linearColor,
        trackColor = ProgressIndicatorDefaults.linearTrackColor,
        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Spacer(modifier = Modifier.height(16.dp))

        status.currentStep?.let { step ->
            Text(
                text = step,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${(status.progress * 100).toInt()}% Complete",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InitializationErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error_24),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Initialization Failed",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onSkip) {
                Text("Continue Offline")
            }

            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}