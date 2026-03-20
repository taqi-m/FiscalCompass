package com.fiscal.compass.presentation.screens.search.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.fiscal.compass.presentation.navigation.MainScreens
import com.fiscal.compass.presentation.navigation.Search
import com.fiscal.compass.presentation.navigation.SearchCategorySelection
import com.fiscal.compass.presentation.navigation.SearchFilters
import com.fiscal.compass.presentation.navigation.SearchGraph
import com.fiscal.compass.presentation.navigation.SearchPersonSelection
import com.fiscal.compass.presentation.navigation.navEnterFromRight
import com.fiscal.compass.presentation.navigation.navExitToRight
import com.fiscal.compass.presentation.navigation.navFadeIn
import com.fiscal.compass.presentation.navigation.navFadeOut
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionEvent
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionScreen
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionViewModel
import com.fiscal.compass.presentation.screens.itemselection.SelectableItem
import com.fiscal.compass.presentation.screens.search.SearchEvent
import com.fiscal.compass.presentation.screens.search.SearchFiltersScreen
import com.fiscal.compass.presentation.screens.search.SearchResultsScreen
import com.fiscal.compass.presentation.screens.search.SearchViewModel

/**
 * NavGraphBuilder extension for the Search nested graph.
 *
 * Search, SearchFilters, SearchCategorySelection and SearchPersonSelection all share ONE
 * SearchViewModel instance scoped to the graph-level NavBackStackEntry.
 * No savedStateHandle round-trips — selection results flow directly through onEvent.
 */
fun NavGraphBuilder.SearchNavGraph(navController: NavHostController) {

    // ── Search nested graph ──────────────────────────────────────────────────
    navigation(
        startDestination = Search.route,
        route = SearchGraph.route,
        enterTransition = { navFadeIn },
        exitTransition = { navFadeOut }
    ) {

        composable(
            route = Search.route,
            enterTransition = { navFadeIn },
            exitTransition = { navFadeOut }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SearchGraph.route)
            }
            val searchViewModel: SearchViewModel = hiltViewModel(graphEntry)
            val state by searchViewModel.state.collectAsState()

            SearchResultsScreen(
                state = state,
                onEvent = searchViewModel::onEvent,
                onNavigate = { navigation ->
                    when (navigation) {
                        is SearchNavigation.NavigateBack ->
                            navController.navigateUp()
                        is SearchNavigation.NavigateToFilters ->
                            navController.navigate(SearchFilters.route)
                        is SearchNavigation.NavigateToTransactionDetail -> {
                            val encodedTransaction = Uri.encode(navigation.transactionJson)
                            navController.navigate(
                                MainScreens.TransactionDetail.passTransaction(encodedTransaction)
                            )
                        }
                        is SearchNavigation.NavigateToCategorySelection ->
                            navController.navigate(SearchCategorySelection.route)
                        is SearchNavigation.NavigateToPersonSelection ->
                            navController.navigate(SearchPersonSelection.route)
                    }
                }
            )
        }

        composable(
            route = SearchFilters.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SearchGraph.route)
            }
            val searchViewModel: SearchViewModel = hiltViewModel(graphEntry)
            val state by searchViewModel.state.collectAsState()

            SearchFiltersScreen(
                state = state,
                onEvent = searchViewModel::onEvent,
                onNavigate = { navigation ->
                    when (navigation) {
                        is SearchNavigation.NavigateBack ->
                            navController.navigateUp()
                        is SearchNavigation.NavigateToCategorySelection ->
                            navController.navigate(SearchCategorySelection.route)
                        is SearchNavigation.NavigateToPersonSelection ->
                            navController.navigate(SearchPersonSelection.route)
                        is SearchNavigation.NavigateToFilters,
                        is SearchNavigation.NavigateToTransactionDetail -> {
                            // These navigation events don't originate from the filters screen
                        }
                    }
                }
            )
        }

        // ── Inline Category Selection ────────────────────────────────────────
        // Items and pre-selection derived from shared SearchViewModel state using
        // tempSearchCriteria.getCategoryIds() — no JSON encoding, no savedStateHandle.
        composable(
            route = SearchCategorySelection.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SearchGraph.route)
            }
            val searchViewModel: SearchViewModel = hiltViewModel(graphEntry)
            val searchState by searchViewModel.state.collectAsState()

            val itemSelectionViewModel: ItemSelectionViewModel = hiltViewModel(backStackEntry)
            val itemSelectionState by itemSelectionViewModel.state.collectAsState()

            val allSelectableCategories = remember(
                searchState.allCategories,
                searchState.tempSearchCriteria
            ) {
                searchState.allCategories.map { category ->
                    SelectableItem(
                        id = category.categoryId,
                        name = category.name,
                        description = category.description,
                        isSelected = searchState.tempSearchCriteria
                            .getCategoryIds().contains(category.categoryId)
                    )
                }
            }

            LaunchedEffect(Unit) {
                itemSelectionViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectableCategories,
                        preSelectedItems = allSelectableCategories.filter { it.isSelected },
                        singleSelectionMode = false
                    )
                )
            }

            ItemSelectionScreen(
                state = itemSelectionState,
                onEvent = itemSelectionViewModel::onEvent,
                title = "Category selection",
                searchPlaceholder = "Search category...",
                singleSelectionMode = false,
                onConfirm = { selectedItems ->
                    searchViewModel.onEvent(
                        SearchEvent.UpdateSelectedCategories(selectedItems.map { it.id })
                    )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // ── Inline Person Selection ──────────────────────────────────────────
        // Items and pre-selection derived from shared SearchViewModel state using
        // tempSearchCriteria.getPersonIds() — no JSON encoding, no savedStateHandle.
        composable(
            route = SearchPersonSelection.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SearchGraph.route)
            }
            val searchViewModel: SearchViewModel = hiltViewModel(graphEntry)
            val searchState by searchViewModel.state.collectAsState()

            val itemSelectionViewModel: ItemSelectionViewModel = hiltViewModel(backStackEntry)
            val itemSelectionState by itemSelectionViewModel.state.collectAsState()

            val allSelectablePersons = remember(
                searchState.allPersons,
                searchState.tempSearchCriteria
            ) {
                searchState.allPersons.map { person ->
                    SelectableItem(
                        id = person.personId,
                        name = person.name,
                        description = person.personType,
                        isSelected = searchState.tempSearchCriteria
                            .getPersonIds().contains(person.personId)
                    )
                }
            }

            LaunchedEffect(Unit) {
                itemSelectionViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectablePersons,
                        preSelectedItems = allSelectablePersons.filter { it.isSelected },
                        singleSelectionMode = false
                    )
                )
            }

            ItemSelectionScreen(
                state = itemSelectionState,
                onEvent = itemSelectionViewModel::onEvent,
                title = "Person selection",
                searchPlaceholder = "Search person...",
                singleSelectionMode = false,
                onConfirm = { selectedItems ->
                    searchViewModel.onEvent(
                        SearchEvent.UpdateSelectedPersons(selectedItems.map { it.id })
                    )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
    // ── End Search nested graph ──────────────────────────────────────────────
}

