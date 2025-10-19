package com.app.uniqueplant.presentation.screens.home.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.navigation.HomeNavGraph
import com.app.uniqueplant.ui.components.buttons.TopBarActionButton
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    state: HomeScreenState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    onEvent(HomeEvent.OnScreenLoad(appNavController))
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var bottomBarVisible by remember { mutableStateOf(true) }

    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y < -5) {
                    // Scrolling down
                    bottomBarVisible = false
                } else if (available.y > 5) {
                    // Scrolling up
                    bottomBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val homeNavController = rememberNavController()
    val items = listOf(
        HomeBottomScreen.Dashboard,
        HomeBottomScreen.Transactions,
        HomeBottomScreen.Analytics
    )

    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
        ?: HomeBottomScreen.Dashboard.route


    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            icon = {
                                Icon(
                                    painterResource(screen.iconResource),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    homeNavController.navigate(screen.route) {
                                        popUpTo(homeNavController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                title = { Text(text = "Unique Stone Plant") },
                actions = { ActionButtons(onEvent = onEvent) }
            )
        },
        /*floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { appNavController.navigate(MainScreens.AddTransaction.route) },
                text = { Text("Add Transaction") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Transaction",
                    )
                }
            )
        }*/
    )
    { paddingValues ->
        HomeNavGraph(
            Modifier
                .padding(top = paddingValues.calculateTopPadding(), start = paddingValues.calculateStartPadding(LocalLayoutDirection.current), end = paddingValues.calculateEndPadding(LocalLayoutDirection.current), bottom = 0.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .nestedScroll(bottomBarScrollConnection),
            homeNavController,
            appNavController
        )
    }
}

@Composable
private fun ActionButtons(
    onEvent: (HomeEvent) -> Unit,
){
    TopBarActionButton(
        onClick = { onEvent(HomeEvent.OnSearchClicked) },
        icon = R.drawable.ic_search_24,
        contentDescription = "Search"
    )
    TopBarActionButton(
        onClick = { onEvent(HomeEvent.OnSettingsClicked) },
        icon = R.drawable.ic_settings_24,
        contentDescription = "Settings"
    )
}

@Preview()
@Composable
fun HomeScreenPreview() {
    UniquePlantTheme {
        HomeScreen(
            appNavController = rememberNavController(),
            HomeScreenState(),
            onEvent = {}
        )
    }
}
