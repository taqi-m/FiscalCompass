package com.app.uniqueplant.presentation.admin.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.navigation.HomeNavGraph
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun HomeScreen(
    appNavController: NavHostController,
    state: HomeScreenState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    val homeNavController =  rememberNavController()
    val items = listOf(
        HomeBottomScreen.Dashboard,
        HomeBottomScreen.Transactions,
        HomeBottomScreen.Analytics
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(screen.iconResource), contentDescription = null) },
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
        }
    ) { paddingValues ->
        HomeNavGraph(homeNavController, appNavController, Modifier.padding(paddingValues))
    }
}


@Preview(
    apiLevel = 33
)
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
