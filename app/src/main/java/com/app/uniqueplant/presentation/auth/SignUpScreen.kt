package com.app.uniqueplant.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.auth.events.SignUpEvent
import com.app.uniqueplant.presentation.auth.states.SignUpScreenState
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun SignUpScreen(
    state: SignUpScreenState,
    onEvent: (SignUpEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message if signup fails
    LaunchedEffect(key1 = state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.error)
        }
    }

    // Navigate if signup is successful
    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(SignUpEvent.EmailChanged(it)) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(SignUpEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onEvent(SignUpEvent.SignUpClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.email.isNotEmpty() && state.password.isNotEmpty()
            ) {
                if(state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign Up")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    onEvent(SignUpEvent.ResetState)
                    onNavigateToLogin()
                },
                enabled = !state.isLoading
            ) {
                Text("Already have an account? Sign In")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    UniquePlantTheme {
        SignUpScreen(
            state = SignUpScreenState(),
            onEvent = {},
            onNavigateToLogin = {}
        )
    }

}
