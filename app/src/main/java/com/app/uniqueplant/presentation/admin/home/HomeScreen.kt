package com.app.uniqueplant.presentation.admin.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.navigation.HomeNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    state: HomeScreenState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    val homeNavController = rememberNavController()
    val items = listOf(
        HomeBottomScreen.Dashboard,
        HomeBottomScreen.Transactions,
        HomeBottomScreen.Analytics,
        HomeBottomScreen.Settings
    )

    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
        ?: HomeBottomScreen.Dashboard.route


    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(screen.iconResource),
                                contentDescription = null
                            )
                        },
                        label = { Text(screen.label) },
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
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    FilledTonalIconButton (
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            homeNavController.navigate(HomeBottomScreen.Dashboard.route) {
                                popUpTo(homeNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_person_24),
                            contentDescription = "Navigate to Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )

                    }
                },
                title = {  },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
//                            onEvent(HomeEvent.OnSettingsClick)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_24),
                            contentDescription = "Settings"
                        )
                    }
                }
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
        HomeNavGraph(homeNavController, appNavController, Modifier.padding(paddingValues))
    }
}


@Preview()
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        appNavController = rememberNavController(),
        HomeScreenState(),
        onEvent = {}
    )
}
