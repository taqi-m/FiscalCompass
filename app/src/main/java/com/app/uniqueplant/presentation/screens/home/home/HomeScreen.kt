package com.app.uniqueplant.presentation.screens.home.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.presentation.navigation.HomeBottomScreen
import com.app.uniqueplant.presentation.navigation.HomeNavGraph
import com.app.uniqueplant.presentation.navigation.MainScreens
import com.app.uniqueplant.ui.components.buttons.TopBarActionButton
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    state: HomeScreenState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    onEvent(HomeEvent.OnScreenLoad(appNavController))
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var isBottomBarVisible by remember { mutableStateOf(true) }

    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y < -5) {
                    // Scrolling down
                    isBottomBarVisible = false
                } else if (available.y > 5) {
                    // Scrolling up
                    isBottomBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val items = mutableListOf<HomeBottomScreen>(
        HomeBottomScreen.Dashboard,
        HomeBottomScreen.Analytics,
    )

    if (state.canViewCategories) {
        items.add(HomeBottomScreen.Categories)
    }
    if (state.canViewPeople) {
        items.add(HomeBottomScreen.People)
    }


    val homeNavController = rememberNavController()

    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
        ?: HomeBottomScreen.Dashboard.route


    Scaffold(
        bottomBar = {
            if (items.size == 1) {
                return@Scaffold
            }
            NavigationBar {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val icon = if (isSelected) screen.selectedIcon else screen.unselectedIcon
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
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
                scrollBehavior = scrollBehavior,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = currentRoute.capitalize(Locale.current),
                        textAlign = TextAlign.Center,
                        )
                },
                actions = {
                    TopBarActionButton(
                        onClick = { onEvent(HomeEvent.OnSearchClicked) },
                        icon = R.drawable.ic_history_24,
                        contentDescription = "Search"
                    )
                },
                navigationIcon = {
                    TopBarActionButton(
                        onClick = { onEvent(HomeEvent.OnSettingsClicked) },
                        icon = R.drawable.ic_settings_24,
                        contentDescription = "Settings"
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { appNavController.navigate(MainScreens.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_24),
                    contentDescription = "Add Transaction"
                )
            }
        }
    )
    { paddingValues ->
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.padding(paddingValues))
        } else {
            HomeNavGraph(
                Modifier
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .nestedScroll(bottomBarScrollConnection),
                homeNavController,
                appNavController
            )
        }
    }
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
