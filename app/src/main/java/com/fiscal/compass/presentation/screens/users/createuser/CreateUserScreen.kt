package com.fiscal.compass.presentation.screens.users.createuser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.rbac.Role as UserRole
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    state: CreateUserScreenState,
    hasPermission: Boolean,
    onEvent: (CreateUserEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(CreateUserEvent.ClearError)
        }
    }

    // Navigate back on success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("User created successfully!")
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (!hasPermission) {
            NoPermissionContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            CreateUserContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = state,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun NoPermissionContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Access Denied",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You don't have permission to create users.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreateUserContent(
    modifier: Modifier = Modifier,
    state: CreateUserScreenState,
    onEvent: (CreateUserEvent) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name Field
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(CreateUserEvent.NameChanged(it)) },
            label = { Text("Full Name") },
            placeholder = { Text("Enter user's full name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(CreateUserEvent.EmailChanged(it)) },
            label = { Text("Email") },
            placeholder = { Text("Enter email address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isLoading
        )

        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(CreateUserEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            placeholder = { Text("Enter password (min 6 characters)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = state.password.isNotBlank() && state.password.length < 6,
            supportingText = {
                if (state.password.isNotBlank() && state.password.length < 6) {
                    Text("Password must be at least 6 characters")
                }
            },
            enabled = !state.isLoading
        )

        // Confirm Password Field
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = { onEvent(CreateUserEvent.ConfirmPasswordChanged(it)) },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = state.confirmPassword.isNotBlank() && state.password != state.confirmPassword,
            supportingText = {
                if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) {
                    Text("Passwords don't match")
                }
            },
            enabled = !state.isLoading
        )

        // Role Selection
        RoleSelectionCard(
            selectedRole = state.selectedRole,
            onRoleSelected = { onEvent(CreateUserEvent.RoleChanged(it)) },
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Create Button
        Button(
            onClick = { onEvent(CreateUserEvent.CreateUserClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isFormValid && !state.isLoading
        ) {
            AnimatedContent(
                targetState = state.isLoading,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "CreateButtonContent"
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create User")
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionCard(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "User Role",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(Modifier.selectableGroup()) {
                UserRole.entries.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedRole == role,
                                onClick = { onRoleSelected(role) },
                                role = Role.RadioButton,
                                enabled = enabled
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = null, // null because the Row handles the click
                            enabled = enabled
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = role.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = getRoleDescription(role),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getRoleDescription(role: UserRole): String {
    return when (role) {
        UserRole.ADMIN -> "Full access to all features including user management"
        UserRole.EMPLOYEE -> "Can add transactions and view own data only"
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true)
@Composable
fun CreateUserScreenPreview() {
    FiscalCompassTheme {
        CreateUserScreen(
            state = CreateUserScreenState(),
            hasPermission = true,
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserScreenPreview_NoPermission() {
    FiscalCompassTheme {
        CreateUserScreen(
            state = CreateUserScreenState(),
            hasPermission = false,
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserScreenPreview_FilledForm() {
    FiscalCompassTheme {
        CreateUserScreen(
            state = CreateUserScreenState(
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                confirmPassword = "password123",
                selectedRole = UserRole.EMPLOYEE
            ),
            hasPermission = true,
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserScreenPreview_Loading() {
    FiscalCompassTheme {
        CreateUserScreen(
            state = CreateUserScreenState(
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                confirmPassword = "password123",
                isLoading = true
            ),
            hasPermission = true,
            onEvent = {},
            onNavigateBack = {}
        )
    }
}

