package com.app.uniqueplant.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.auth.events.LoginEvent
import com.app.uniqueplant.presentation.auth.states.LoginScreenState
import com.app.uniqueplant.ui.components.BottomSpikes
import com.app.uniqueplant.ui.components.MountainSpikes
import com.app.uniqueplant.ui.components.RelativeCircle
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun LoginScreen(
    state: LoginScreenState,
    onEvent: (LoginEvent) -> Unit,
    isUserLoggedIn: () -> Boolean,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Track the position and size of the sign in prompt text
    var promptTextPosition by remember { mutableStateOf(Offset.Zero) }
    var promptTextHeight by remember { mutableFloatStateOf(0f) }

    // Check if user is already logged in
    LaunchedEffect(key1 = true) {
        if (isUserLoggedIn()) {
            onLoginSuccess()
        }
    }

    // Show error message if login fails
    LaunchedEffect(key1 = state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.error)
        }
    }

    // Navigate if login is successful
    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = bottomPadding),
                hostState = snackbarHostState
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Allow content to draw behind navigation
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
                    .padding(padding)
                    .consumeWindowInsets(padding) // Consume insets to prevent double padding
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sample_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(bottom = 16.dp) // Add some space below the logo
                        .clip(CircleShape)
                        .size(96.dp) // Adjust size as needed
                )
                Text(
                    text = stringResource(R.string.sign_in_headline),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.sign_in_prompt),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // Store the position and height of the prompt text
                        promptTextPosition = coordinates.positionInRoot()
                        promptTextHeight = coordinates.size.height.toFloat()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                        onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
                        placeholder = { Text(stringResource(R.string.email_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp), // Limit width for better readability on large screens
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_email_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
//                        trailingIcon = {
//                            if (state.email.isNotEmpty()) {
//                                IconButton(
//                                    onClick = {
//                                        onEvent(LoginEvent.EmailChanged(""))
//                                    }
//                                ) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.ic_clear_24),
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .padding(8.dp),
//                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                    )
//                                }
//
//                            }
//                        }
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
                        onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                        placeholder = { Text(stringResource(R.string.password_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp), // Limit width for better readability on large screens
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_password_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
//                        trailingIcon = {
//                            if (state.password.isNotEmpty()) {
//                                Icon(
//                                    painter = painterResource(R.drawable.ic_clear_24),
//                                    contentDescription = null,
//                                    modifier = Modifier
//                                        .padding(8.dp)
//                                        .clickable { onEvent(LoginEvent.PasswordChanged("")) },
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
//                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!state.isLoading)
                            onEvent(LoginEvent.LoginClicked)
                    },
                    modifier = Modifier.widthIn(min = 250.dp, max = 250.dp),
                    enabled = state.email.isNotEmpty() && state.password.isNotEmpty()
                ) {
                    AnimatedContent(
                        targetState = state.isLoading,
                        transitionSpec = {
                            if (targetState > initialState) {
                                // If the target number is larger, it slides up and fades in
                                // while the initial (smaller) number slides up and fades out.
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                // If the target number is smaller, it slides down and fades in
                                // while the initial number slides down and fades out.
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }.using(
                                // Disable clipping since the faded slide-in/out should
                                // be displayed out of bounds.
                                SizeTransform(clip = false)
                            )
                        }, label = "LoginButtonContent"
                    ) { isLoading ->
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp), // Ensure same height as text
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.login_button))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        onEvent(LoginEvent.ResetState)
                        onNavigateToSignUp()
                    }, enabled = !state.isLoading
                ) {
                    Text(stringResource(R.string.sign_up_suggestion))
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE, showSystemUi = true,
    device = "spec:parent=pixel_5,navigation=gesture",
)
@Composable
fun LoginScreenPreview() {
    UniquePlantTheme {
        LoginScreen(
            state = LoginScreenState(
                email = "secret@s.com",
                password = "11",
            ),
            onEvent = {},
            isUserLoggedIn = { false },
            onLoginSuccess = {},
            onNavigateToSignUp = {})
    }
}