package com.fiscal.compass.presentation.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.base.User
import com.fiscal.compass.domain.model.rbac.Role
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import java.util.Date

/**
 * Main User Screen Composable.
 * Implements the "Approach 3" pattern where the initial load is triggered once,
 * and subsequent state changes (Refresh/Paginate) are driven by the UI.
 */
@Composable
fun UserScreen(
    state: UserScreenState,
    canManageUsers: Boolean = false,
    onEvent: (UserEvent) -> Unit,
    onNavigateToCreateUser: () -> Unit = {}
) {
    // 1. Initial Load Logic
    // Triggers only when the composable enters the composition.
    // The ViewModel's "if (displayState == null)" check ensures this doesn't reload on rotation.
    LaunchedEffect(Unit) {
        onEvent(UserEvent.LoadUsers)
    }
    UserScreenContent(
        state = state,
        canManageUsers = canManageUsers,
        onEvent = onEvent,
        onNavigateToCreateUser = onNavigateToCreateUser
    )
}

/**
 * State Switcher
 * Handles the high-level mutually exclusive states: Loading, Error, and Content.
 */
@Composable
fun UserScreenContent(
    state: UserScreenState,
    canManageUsers: Boolean,
    onEvent: (UserEvent) -> Unit,
    onNavigateToCreateUser: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            if (canManageUsers) {
                FloatingActionButton(
                    onClick = onNavigateToCreateUser,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create User"
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val displayState = state.displayState) {
                is DisplayState.Loading -> LoadingScreen()
                is DisplayState.Error -> ErrorScreen(onRetry = { onEvent(UserEvent.LoadUsers) })
                is DisplayState.Content -> ContentScreen(
                    content = displayState,
                    onEvent = onEvent
                )
                null -> LoadingScreen() // Handle initial null state (idle)
            }
        }
    }
}

/**
 * Content Screen
 * Displays the list of users and handles shared states (Refreshing, Paginating).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    content: DisplayState.Content,
    onEvent: (UserEvent) -> Unit
) {
    val users = content.userResults.users
    val canLoadMore = content.userResults.canLoadMore

    // Check specific sub-states from ContentDisplayState
    val subState = content.contentDisplayState
    val isRefreshing = subState is ContentDisplayState.Refreshing
    val isPaginating = subState is ContentDisplayState.Paginating
    val isPaginationError = subState is ContentDisplayState.PaginationError

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            // TODO: Add 'object Refresh : UserEvent()' to your UserEvent sealed class
            // onEvent(UserEvent.Refresh)

            // Fallback for now if event doesn't exist yet:
            onEvent(UserEvent.LoadUsers)
        }
    ) {
        val listState = rememberLazyListState()

        // Pagination Logic: Trigger when scrolling near the end
        val shouldPaginate by remember {
            derivedStateOf {
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val buffer = 3

                canLoadMore &&
                        (totalItems - lastVisibleItem <= buffer) &&
                        subState == null // Only paginate if not already loading/error
            }
        }

        LaunchedEffect(shouldPaginate) {
            if (shouldPaginate) {
                // TODO: Add 'object Paginate : UserEvent()' to your UserEvent sealed class
                // onEvent(UserEvent.Paginate)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                UserItem(user = user)
            }

            // Footer: Pagination Loading
            if (isPaginating) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Footer: Pagination Error
            if (isPaginationError) {
                item {
                    PaginationErrorFooter(onRetry = {
                        // onEvent(UserEvent.Paginate)
                    })
                }
            }
        }
    }
}

/**
 * Single User List Item
 * Maps fields strictly from the provided User.kt model.
 */
@Composable
fun UserItem(user: User) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture Placeholder
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (user.profilePictureUrl != null) {
                        // Note: Use Coil (AsyncImage) here if dependency is available.
                        // For now, using text initials as a safe fallback without extra deps.
                        Text(
                            text = user.username.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Username
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Email
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = user.userType.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PaginationErrorFooter(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Could not load more",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Button(
            modifier = Modifier.padding(start = 8.dp),
            shape = MaterialTheme.shapes.medium,
            onClick = onRetry
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Failed to load users",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Idle (null displayState)")
@Composable
fun UserScreenPreview_Idle() {
    FiscalCompassTheme {
        UserScreen(
            state = UserScreenState(displayState = null),
            canManageUsers = true,
            onEvent = {},
            onNavigateToCreateUser = {}
        )
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
fun UserScreenPreview_Content() {
    val mockUsers = listOf(
        User(
            userId = "1",
            username = "john_doe",
            email = "john@example.com",
            userType = Role.ADMIN,
            createdAt = Date(),
            updatedAt = Date()
        ),
        User(
            userId = "2",
            username = "jane_smith",
            email = "jane@example.com",
            userType = Role.EMPLOYEE,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    val contentState = DisplayState.Content(
        userResults = UserResults(users = mockUsers, canLoadMore = true),
        contentDisplayState = null
    )
    FiscalCompassTheme {
        UserScreen(
            state = UserScreenState(displayState = contentState),
            canManageUsers = true,
            onEvent = {},
            onNavigateToCreateUser = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Refreshing")
@Composable
fun UserScreenPreview_Refreshing() {
    val mockUsers = listOf(
        User(
            userId = "1",
            username = "john_doe",
            email = "john@example.com",
            userType = Role.ADMIN,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    val contentState = DisplayState.Content(
        userResults = UserResults(users = mockUsers, canLoadMore = true),
        contentDisplayState = ContentDisplayState.Refreshing
    )
    FiscalCompassTheme {
        UserScreen(
            state = UserScreenState(displayState = contentState),
            canManageUsers = true,
            onEvent = {},
            onNavigateToCreateUser = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Paginating")
@Composable
fun UserScreenPreview_Paginating() {
    val mockUsers = listOf(
        User(
            userId = "1",
            username = "john_doe",
            email = "john@example.com",
            userType = Role.ADMIN,
            createdAt = Date(),
            updatedAt = Date()
        ),
        User(
            userId = "2",
            username = "jane_smith",
            email = "jane@example.com",
            userType = Role.EMPLOYEE,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    val contentState = DisplayState.Content(
        userResults = UserResults(users = mockUsers, canLoadMore = true),
        contentDisplayState = ContentDisplayState.Paginating
    )
    FiscalCompassTheme {
        UserScreen(
            state = UserScreenState(displayState = contentState),
            canManageUsers = true,
            onEvent = {},
            onNavigateToCreateUser = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Pagination Error")
@Composable
fun UserScreenPreview_PaginationError() {
    val mockUsers = listOf(
        User(
            userId = "1",
            username = "john_doe",
            email = "john@example.com",
            userType = Role.ADMIN,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    val contentState = DisplayState.Content(
        userResults = UserResults(users = mockUsers, canLoadMore = true),
        contentDisplayState = ContentDisplayState.PaginationError
    )
    FiscalCompassTheme {
        UserScreen(
            state = UserScreenState(displayState = contentState),
            canManageUsers = false,
            onEvent = {},
            onNavigateToCreateUser = {}
        )
    }
}