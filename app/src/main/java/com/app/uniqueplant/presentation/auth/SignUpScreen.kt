package com.app.uniqueplant.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.auth.events.SignUpEvent
import com.app.uniqueplant.presentation.auth.states.SignUpScreenState
import com.app.uniqueplant.ui.components.MountainSpikes
import com.app.uniqueplant.ui.components.RelativeCircle
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun SignUpScreen(
    state: SignUpScreenState,
    onEvent: (SignUpEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Track the position and size of the sign in prompt text
    var promptTextPosition by remember { mutableStateOf(Offset.Zero) }
    var promptTextHeight by remember { mutableFloatStateOf(0f) }

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
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = bottomPadding),
                hostState = snackbarHostState
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            //Relative circle at the top
            RelativeCircle(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Adjust height as needed
                    .align(Alignment.TopCenter),
                promptTextPosition = promptTextPosition,
                promptTextHeight = promptTextHeight
            )

            // Mountain spikes at the bottom
            MountainSpikes(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp + bottomPadding) // Adjust height as needed
                    .align(Alignment.BottomCenter)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(paddingValues = padding)
                    .consumeWindowInsets(padding)
                    .padding(16.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.sign_up_headline),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // Store the position and height of the prompt text
                        promptTextPosition = coordinates.positionInRoot()
                        promptTextHeight = coordinates.size.height.toFloat()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.username_label),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                OutlinedTextField(
                    value = state.username,
                    onValueChange = { onEvent(SignUpEvent.UsernameChanged(it)) },
                    label = { Text(stringResource(R.string.username_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.email_label),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onEvent(SignUpEvent.EmailChanged(it)) },
                        label = { Text(stringResource(R.string.email_placeholder)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))



                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.password_label),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onEvent(SignUpEvent.PasswordChanged(it)) },
                        label = { Text(stringResource(R.string.password_placeholder)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onEvent(SignUpEvent.SignUpClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && state.email.isNotEmpty() && state.password.isNotEmpty()
                ) {
                    if (state.isLoading) {
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
