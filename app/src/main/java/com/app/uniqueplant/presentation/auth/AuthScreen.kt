package com.app.uniqueplant.presentation.auth

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.components.FormTextField
import com.app.uniqueplant.ui.components.MountainSpikes
import com.app.uniqueplant.ui.components.RelativeCircle
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun AuthScreen(
    appNavController: NavHostController,
    state: AuthScreenState,
    onEvent: (AuthEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Track the position and size of the sign in prompt text
    var promptTextPosition by remember { mutableStateOf(Offset.Zero) }
    var promptTextHeight by remember { mutableFloatStateOf(0f) }


    // Show error message if login fails
    LaunchedEffect(key1 = state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.error)
        }
    }

    // Navigate if login is successful
    LaunchedEffect(key1 = state.isSuccess) {
        Log.d("AuthScreen", "Auth Success Detected")
        if (state.isSuccess) {
            Log.d("AuthScreen", "Login Success Called")
            onEvent(AuthEvent.LoginSuccess(appNavController))
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = bottomPadding),
                hostState = snackbarHostState
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            //Relative circle at the top
            RelativeCircle(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.TopCenter),
                promptTextPosition = promptTextPosition,
                promptTextHeight = promptTextHeight
            )

            // Mountain spikes at the bottom
            MountainSpikes(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp + bottomPadding)
                    .align(Alignment.BottomCenter)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sample_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clip(CircleShape)
                        .size(96.dp)
                )
                Text(
                    text = stringResource(
                        if (state.isSignUp) R.string.sign_up_headline else R.string.sign_in_headline
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(
                        if (state.isSignUp) R.string.sign_up_prompt else R.string.sign_in_prompt
                    ),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // Store the position and height of the prompt text
                        promptTextPosition = coordinates.positionInRoot()
                        promptTextHeight = coordinates.size.height.toFloat()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column (
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                )
                {
                    AnimatedContent(
                        targetState = state.isSignUp,
                        label = "UsernameFieldAnimation"
                    ) { isSignUp ->
                        if (isSignUp) {
                            FormTextField(
                                value = state.username,
                                label = stringResource(R.string.username_label),
                                placeholder = stringResource(R.string.username_placeholder),
                                onValueChange = { onEvent(AuthEvent.UsernameChanged(it)) },
                                keyboardType = KeyboardType.Text,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_person_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                imeAction = ImeAction.Next
                            )
                        } else {
                            // Empty composable when not showing username field
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }


                    FormTextField(
                        value = state.email,
                        label = stringResource(R.string.email_label),
                        placeholder = stringResource(R.string.email_placeholder),
                        onValueChange = { onEvent(AuthEvent.EmailChanged(it)) },
                        keyboardType = KeyboardType.Email,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_email_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        imeAction = ImeAction.Next
                    )


                    FormTextField(
                        value = state.password,
                        label = stringResource(R.string.password_label),
                        placeholder = stringResource(R.string.password_placeholder),
                        onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isPassword = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_password_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!state.isLoading)
                        {
                            if (state.isSignUp) {
                                onEvent(AuthEvent.SignUpClicked)
                            } else {
                                onEvent(AuthEvent.LoginClicked)
                            }
                        }
                    },
                    modifier = Modifier.widthIn(min = 250.dp, max = 250.dp),
                    enabled = state.email.isNotEmpty() && state.password.isNotEmpty() && if (state.isSignUp) state.username.isNotEmpty() else true
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    )
                    {
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
                                Text(
                                    stringResource(
                                        if (state.isSignUp) R.string.sign_up_button else R.string.sign_in_button
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        onEvent(AuthEvent.SwitchState)
                    },
                    enabled = !state.isLoading
                ) {
                    Text(
                        stringResource(
                            if (state.isSignUp) R.string.already_have_account else R.string.create_account
                        )
                    )
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
fun AuthScreenPreview() {
    UniquePlantTheme {
        AuthScreen(
            state = AuthScreenState(
                email = "secret@s.com",
                password = "11",
                isSignUp = true
            ),
            onEvent = {},
            appNavController = rememberNavController(),
        )
    }
}