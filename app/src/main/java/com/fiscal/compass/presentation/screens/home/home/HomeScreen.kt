package com.fiscal.compass.presentation.screens.home.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.fiscal.compass.R
import com.fiscal.compass.presentation.navigation.AddTransactionGraph
import com.fiscal.compass.presentation.navigation.HomeBottomScreen
import com.fiscal.compass.presentation.navigation.HomeNavGraph
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.ui.components.buttons.TopBarActionButton
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    state: HomeScreenState,
    onEvent: (HomeEvent) -> Unit = {},
    onSyncClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var isBottomBarVisible by remember { mutableStateOf(true) }

    val bottomBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -5) isBottomBarVisible = false
                else if (available.y > 5) isBottomBarVisible = true
                return Offset.Zero
            }
        }
    }

    val items = mutableListOf(
        HomeBottomScreen.Dashboard,
        HomeBottomScreen.Analytics,
    )
    if (state.canViewCategories) items.add(HomeBottomScreen.Categories)
    if (state.canViewPeople) {
        items.add(HomeBottomScreen.People)
        items.add(HomeBottomScreen.Users)
    }

    val homeNavController = rememberNavController()
    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route
        ?: HomeBottomScreen.Dashboard.route

    val fabConfig by remember(currentRoute, state) {
        derivedStateOf {
            val primaryAddTransaction = FabAction(
                iconRes = R.drawable.ic_add_24,
                label = "Transaction",
                contentDescription = "Add Transaction",
                onClick = { appNavController.navigate(AddTransactionGraph.route) }
            )

            when (currentRoute) {
                HomeBottomScreen.Dashboard.route,
                HomeBottomScreen.Analytics.route,
                HomeBottomScreen.Categories.route -> FabConfig(
                    primary = primaryAddTransaction,
                    secondary = null
                )

                HomeBottomScreen.People.route -> FabConfig(
                    primary = primaryAddTransaction,
                    secondary = if (state.canAddPerson) FabAction(
                        iconRes = R.drawable.ic_add_24,
                        label = "Person",
                        contentDescription = "Add Person",
                        onClick = {
                            appNavController.navigate(
                                MainScreens.AddPerson.passSelectedType(
                                    "CUSTOMER"
                                )
                            )
                        }
                    ) else null
                )

                HomeBottomScreen.Users.route -> FabConfig(
                    primary = primaryAddTransaction,
                    secondary = if (state.canManageUsers) FabAction(
                        iconRes = R.drawable.ic_add_24,
                        label = "User",
                        contentDescription = "Create User",
                        onClick = { appNavController.navigate(MainScreens.CreateUser.route) }
                    ) else null
                )

                else -> null
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            fabConfig?.let { config ->
                WhatsAppStyleFab(config = config)
            }
        },
        bottomBar = {
            if (items.size > 1) {
                CustomBottomNavigation(
                    items = items,
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        homeNavController.navigate(screen.route) {
                            popUpTo(homeNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
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
                        icon = R.drawable.ic_cloud_sync_24,
                        contentDescription = "Sync Data",
                        onClick = onSyncClick,
                    )
                    TopBarActionButton(
                        onClick = onSearchClick,
                        icon = R.drawable.ic_history_24,
                        contentDescription = "Search"
                    )
                },
                navigationIcon = {
                    TopBarActionButton(
                        onClick = onSettingsClick,
                        icon = R.drawable.ic_settings_24,
                        contentDescription = "Settings"
                    )
                }
            )
        },
    ) { paddingValues ->
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.padding(paddingValues))
        } else {
            HomeNavGraph(
                modifier = Modifier
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .nestedScroll(bottomBarScrollConnection),
                homeNavController = homeNavController,
                appNavController = appNavController,
            )
        }
    }
}

@Preview()
@Composable
fun HomeScreenPreview() {

    FiscalCompassTheme {
        HomeScreen(
            appNavController = rememberNavController(),
            HomeScreenState(
                canViewPeople = true,
                canViewCategories = true
            ),
            onEvent = {}
        )
    }
}


@Composable
fun CustomBottomNavigation(
    items: List<HomeBottomScreen>,
    currentRoute: String,
    onItemClick: (HomeBottomScreen) -> Unit
) {
    // Floating container
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStartPercent = 25,
            topEndPercent = 25,
            bottomStartPercent = 0,
            bottomEndPercent = 0
        ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route

                // Individual Item
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onItemClick(screen) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(if (isSelected) screen.selectedIcon else screen.unselectedIcon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // This part makes the label slide in/out
                        AnimatedVisibility(visible = isSelected) {
                            Text(
                                text = screen.label,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WhatsAppStyleFab(
    config: FabConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp) // Slightly tighter spacing
    ) {
        // ONLY the secondary action is animated
        AnimatedVisibility(
            visible = config.secondary != null,
            enter = fadeIn() + slideInVertically { it / 2 }, // Half-height slide for subtler entry
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            config.secondary?.let { secondary ->
                // Secondary Extended FAB (Customized to be smaller)
                ExtendedFloatingActionButton(
                    onClick = secondary.onClick,
                    // Use a different color to distinguish from Primary
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    // Smaller height and padding to make it "relatively small"
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    icon = {
                        Icon(
                            painter = painterResource(secondary.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp) // Smaller icon
                        )
                    },
                    text = {
                        Text(
                            text = secondary.label,
                            style = MaterialTheme.typography.labelLarge // Clean, small text
                        )
                    }
                )
            }
        }

        // Primary FAB is NOT animated; stays static
        ExtendedFloatingActionButton(
            onClick = config.primary.onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {
                Icon(
                    painter = painterResource(config.primary.iconRes),
                    contentDescription = config.primary.contentDescription
                )
            },
            text = { Text(text = config.primary.label) }
        )
    }
}


@Preview
@Composable
fun WhatsAppStyleFabPreview() {
    FiscalCompassTheme {
        WhatsAppStyleFab(
            config = FabConfig(
                primary = FabAction(
                    iconRes = R.drawable.ic_add_24,
                    label = "Transaction",
                    contentDescription = "Add Transaction",
                    onClick = {}
                ),
                secondary = FabAction(
                    iconRes = R.drawable.ic_add_24,
                    label = "Person",
                    contentDescription = "Add Person",
                    onClick = {}
                )
            )
        )
    }
}