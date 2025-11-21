package com.app.uniqueplant.presentation.navigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.auth.AuthScreen
import com.app.uniqueplant.presentation.screens.auth.AuthViewModel
import com.app.uniqueplant.presentation.screens.category.CategoriesScreen
import com.app.uniqueplant.presentation.screens.category.CategoriesViewModel
import com.app.uniqueplant.presentation.screens.home.home.HomeScreen
import com.app.uniqueplant.presentation.screens.home.home.HomeViewModel
import com.app.uniqueplant.presentation.screens.itemselection.ItemSelectionEvent
import com.app.uniqueplant.presentation.screens.itemselection.ItemSelectionScreen
import com.app.uniqueplant.presentation.screens.itemselection.ItemSelectionViewModel
import com.app.uniqueplant.presentation.screens.itemselection.SelectableItem
import com.app.uniqueplant.presentation.screens.jobs.JobsScreen
import com.app.uniqueplant.presentation.screens.jobs.JobsViewModel
import com.app.uniqueplant.presentation.screens.person.PersonScreen
import com.app.uniqueplant.presentation.screens.person.PersonViewModel
import com.app.uniqueplant.presentation.screens.search.SearchScreen
import com.app.uniqueplant.presentation.screens.search.SearchViewModel
import com.app.uniqueplant.presentation.screens.settings.SettingsScreen
import com.app.uniqueplant.presentation.screens.settings.SettingsViewModel
import com.app.uniqueplant.presentation.screens.sync.SyncScreen
import com.app.uniqueplant.presentation.screens.sync.SyncViewModel
import com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction.AddTransactionScreen
import com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction.AddTransactionViewModel
import com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails.TransactionDetailsScreen
import com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails.TransactionDetailsViewModel
import com.google.gson.Gson


// Animation duration constant for consistent transitions
private const val TRANSITION_DURATION = 400
private const val FADE_TRANSITION_DURATION = (TRANSITION_DURATION * 1.5).toInt()

// Pre-defined transition animations
private val enterFromLeft = fadeIn(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideInHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> -fullWidth }

private val enterFromRight = fadeIn(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideInHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> fullWidth }

private val enterFromUp = fadeIn(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideInVertically(animationSpec = tween(TRANSITION_DURATION)) { fullHeight -> fullHeight }

private val exitToLeft = fadeOut(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideOutHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> -fullWidth }

private val exitToRight = fadeOut(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideOutHorizontally(animationSpec = tween(TRANSITION_DURATION)) { fullWidth -> fullWidth }

private val exitToDown = fadeOut(animationSpec = tween(FADE_TRANSITION_DURATION)) +
        slideOutVertically(animationSpec = tween(TRANSITION_DURATION)) { fullHeight -> fullHeight }

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavigation(
    navController: NavHostController,
    prefs: AppPreferenceRepository
) {

    NavHost(
        navController = navController,
        startDestination =
            if (prefs.isUserLoggedIn()) {
                MainScreens.AdminHome.route
                /*when (prefs.getUserType()) {
                    "admin" -> MainScreens.AdminHome.route
                    "employee" -> MainScreens.EmployeeHome.route
                    else -> MainScreens.Auth.route
                }*/
            } else {
                MainScreens.Auth.route
            }
    ) {

        composable(
            route = MainScreens.Auth.route,
        ) { backStackEntry ->
            val authViewModel: AuthViewModel = hiltViewModel(backStackEntry)
            val authState by authViewModel.state.collectAsState()
            val initializationStatus by authViewModel.initializationStatus.collectAsState()

            AuthScreen(
                appNavController = navController,
                state = authState,
                initializationStatus = initializationStatus,
                onEvent = authViewModel::onEvent,
            )
        }

        composable(
            route = MainScreens.Home.route,
        ) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(route = MainScreens.EmployeeHome.route) {
            // User home screen content
        }

        composable(
            route = MainScreens.AdminHome.route,
        ) { backStackEntry ->
            val homeViewModel: HomeViewModel = hiltViewModel(backStackEntry)
            val homeState by homeViewModel.state.collectAsState()
            HomeScreen(
                appNavController = navController,
                state = homeState,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.AddTransaction.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) { backStackEntry ->
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(backStackEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()
            AddTransactionScreen(
                appNavController = navController,
                state = addTransactionState,
                onEvent = addTransactionViewModel::onEvent
            )
        }

        composable(
            MainScreens.Settings.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }) { backStackEntry ->
            val settingsViewModel: SettingsViewModel = hiltViewModel(backStackEntry)
            val state by settingsViewModel.state.collectAsState()
            SettingsScreen(
                state = state,
                onEvent = settingsViewModel::onEvent,
                onLogout = { route ->
                    navController.navigate(MainScreens.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(
            MainScreens.Sync.route,
            enterTransition = { enterFromUp },
            exitTransition = { exitToDown },
            popEnterTransition = { enterFromUp },
            popExitTransition = { exitToDown }) { backStackEntry ->
            val syncViewModel: SyncViewModel = hiltViewModel(backStackEntry)
            val state by syncViewModel.state.collectAsState()
            SyncScreen(
                state = state,
                onEvent = syncViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.Categories.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) { backStackEntry ->
            val categoriesViewModel: CategoriesViewModel = hiltViewModel(backStackEntry)
            val state by categoriesViewModel.state.collectAsState()
            CategoriesScreen(
                appNavController = navController,
                state = state,
                onEvent = categoriesViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.Person.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }) {
            val personViewModel: PersonViewModel = hiltViewModel()
            val state by personViewModel.state.collectAsState()
            PersonScreen(
                state = state,
                onEvent = personViewModel::onEvent,
            )
        }

        composable(
            route = MainScreens.Jobs.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }
        ) {
            val jobsViewModel: JobsViewModel = hiltViewModel()
            val state by jobsViewModel.state.collectAsState()
            JobsScreen(
                state = state,
                onEvent = jobsViewModel::onEvent
            )
        }

        composable(
            route = MainScreens.TransactionDetail.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }
        ) { backstackEntry ->
            val transactionJson = backstackEntry.arguments?.getString("transaction")
            val transactionUi = Gson().fromJson(transactionJson, TransactionUi::class.java)
            val transactionDetailsViewModel: TransactionDetailsViewModel = hiltViewModel()
            val state by transactionDetailsViewModel.state.collectAsState()
            TransactionDetailsScreen(
                state = state,
                onEvent = transactionDetailsViewModel::onEvent,
                onBackPressed = {
                    navController.popBackStack()
                },
                transactionUi = transactionUi,
            )
        }

        composable(
            route = MainScreens.Search.route,
            enterTransition = { enterFromRight },
            exitTransition = { exitToRight },
            popEnterTransition = { enterFromRight },
            popExitTransition = { exitToRight }
        ) {
            val searchViewModel: SearchViewModel = hiltViewModel()
            val state by searchViewModel.state.collectAsState()
            SearchScreen(
                state = state,
                onEvent = searchViewModel::onEvent,
                appNavController = navController
            )
        }

        composable(
            route = MainScreens.MultiSelection.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }
        ) { backStackEntry ->
            val navKey = backStackEntry.arguments?.getString("navKey") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val allSelectableItemsJson = backStackEntry.arguments?.getString("allIds")
            val decodedJson = allSelectableItemsJson?.let { Uri.decode(it) }
            val allSelectableItems = if (!decodedJson.isNullOrEmpty()) {
                try {
                    Gson().fromJson(decodedJson, Array<SelectableItem>::class.java).toList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val itemSelectionViewModel: ItemSelectionViewModel = hiltViewModel()
            val itemSelectionState by itemSelectionViewModel.state.collectAsState()

            val preSelectedSelectableItems =
                remember(allSelectableItems) {
                    allSelectableItems.filter { selectableItem ->
                        selectableItem.isSelected
                    }
                }

            // Initialize the screen
            if (itemSelectionState.allItems.isEmpty()) {
                itemSelectionViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectableItems,
                        preSelectedItems = preSelectedSelectableItems
                    )
                )
            }

            ItemSelectionScreen(
                state = itemSelectionState,
                onEvent = itemSelectionViewModel::onEvent,
                title = "${title.capitalize(Locale.current)} selection",
                searchPlaceholder = "Search $title...",
                onConfirm = { selectedCategories ->
                    val selectedIds = selectedCategories.joinToString(",") { it.id }
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(navKey, selectedIds)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = MainScreens.PersonSelection.route,
            enterTransition = { enterFromLeft },
            exitTransition = { exitToLeft },
            popEnterTransition = { enterFromLeft },
            popExitTransition = { exitToLeft }
        ) { backStackEntry ->
            val preSelectedIdsString = backStackEntry.arguments?.getString("preSelectedIds") ?: ""
            val preSelectedIds = if (preSelectedIdsString.isEmpty()) emptyList()
            else preSelectedIdsString.split(",").map { it.toLong() }

            val searchViewModel: SearchViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(MainScreens.Search.route) }
            )
            val searchState by searchViewModel.state.collectAsState()

            val personViewModel: ItemSelectionViewModel = hiltViewModel()
            val personState by personViewModel.state.collectAsState()

            val allSelectablePersons = remember(searchState.allPersons) {
                searchState.allPersons.map { person ->
                    SelectableItem(
                        id = person.personId.toString(),
                        name = person.name
                    )
                }
            }

            val preSelectedSelectablePersons = remember(allSelectablePersons, preSelectedIds) {
                allSelectablePersons.filter { selectableItem ->
                    preSelectedIds.contains(selectableItem.id.toLong())
                }
            }

            // Initialize the screen
            if (personState.allItems.isEmpty()) {
                personViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectablePersons,
                        preSelectedItems = preSelectedSelectablePersons
                    )
                )
            }

            ItemSelectionScreen(
                state = personState,
                onEvent = personViewModel::onEvent,
                title = "Select Persons",
                searchPlaceholder = "Search persons...",
                onConfirm = { selectedPersons ->
                    val selectedIds = selectedPersons.joinToString(",") { it.id }
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedPersonIds", selectedIds)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

    }
}
