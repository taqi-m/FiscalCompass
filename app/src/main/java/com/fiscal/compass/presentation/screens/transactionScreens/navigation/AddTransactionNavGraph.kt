package com.fiscal.compass.presentation.screens.transactionScreens.navigation

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
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.navigation.AddTransaction
import com.fiscal.compass.presentation.navigation.AddTransactionGraph
import com.fiscal.compass.presentation.navigation.AddTxCategorySelection
import com.fiscal.compass.presentation.navigation.AddTxPersonSelection
import com.fiscal.compass.presentation.navigation.Amount
import com.fiscal.compass.presentation.navigation.EditAmount
import com.fiscal.compass.presentation.navigation.SearchGraph
import com.fiscal.compass.presentation.navigation.navEnterFromRight
import com.fiscal.compass.presentation.navigation.navExitToRight
import com.fiscal.compass.presentation.navigation.navFadeIn
import com.fiscal.compass.presentation.navigation.navFadeOut
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionEvent
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionScreen
import com.fiscal.compass.presentation.screens.itemselection.ItemSelectionViewModel
import com.fiscal.compass.presentation.screens.itemselection.SelectableItem
import com.fiscal.compass.presentation.screens.transactionScreens.addTransaction.AddTransactionEvent
import com.fiscal.compass.presentation.screens.transactionScreens.addTransaction.AddTransactionNavigation
import com.fiscal.compass.presentation.screens.transactionScreens.addTransaction.AddTransactionScreen
import com.fiscal.compass.presentation.screens.transactionScreens.addTransaction.AddTransactionViewModel
import com.fiscal.compass.presentation.screens.transactionScreens.amountScreen.AmountScreen
import com.fiscal.compass.presentation.screens.transactionScreens.amountScreen.AmountViewModel
import com.google.gson.Gson

/**
 * NavGraphBuilder extension for the AddTransaction nested graph.
 *
 * AddTransaction, Amount, AddTxCategorySelection and AddTxPersonSelection all share ONE
 * AddTransactionViewModel instance scoped to the graph-level NavBackStackEntry.
 *
 * EditAmount is a standalone flat composable registered here because it belongs
 * conceptually to the AddTransaction flow (edit path from TransactionDetailsScreen).
 */
fun NavGraphBuilder.AddTransactionNavGraph(navController: NavHostController) {

    // ── AddTransaction nested graph ──────────────────────────────────────────
    navigation(
        startDestination = AddTransaction.route,
        route = AddTransactionGraph.route,
        enterTransition = { navFadeIn },
        exitTransition = { navFadeOut }
    ) {

        composable(
            route = AddTransaction.route,
            enterTransition = { navFadeIn },
            exitTransition = { navFadeOut }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AddTransactionGraph.route)
            }
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(graphEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()

            AddTransactionScreen(
                state = addTransactionState,
                onEvent = addTransactionViewModel::onEvent,
                onNavigate = { navigation ->
                    when (navigation) {
                        is AddTransactionNavigation.NavigateBack ->
                            navController.navigateUp()

                        is AddTransactionNavigation.NavigateToCategorySelection ->
                            navController.navigate(AddTxCategorySelection.route)

                        is AddTransactionNavigation.NavigateToPersonSelection ->
                            navController.navigate(AddTxPersonSelection.route)

                        is AddTransactionNavigation.NavigateToAmountScreen -> {
                            addTransactionViewModel.onEvent(
                                AddTransactionEvent.SetEditMode(navigation.editMode)
                            )
                            navController.navigate(Amount.navigate(navigation.editMode))
                        }
                    }
                }
            )
        }

        composable(
            route = Amount.route,
            enterTransition = { navFadeIn },
            exitTransition = { navFadeOut }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AddTransactionGraph.route)
            }
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(graphEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()

            val amountViewModel: AmountViewModel = hiltViewModel(backStackEntry)
            val amountState by amountViewModel.state.collectAsState()

            // Read editMode from nav arg; transaction comes from shared ViewModel state
            val editMode = remember(backStackEntry) {
                backStackEntry.arguments?.getString("edit")?.toBoolean() ?: false
            }

            // Seed AmountViewModel once with the shared transaction + editMode
            LaunchedEffect(Unit) {
                amountViewModel.loadTransaction(addTransactionState.transaction, editMode)
            }

            AmountScreen(
                state = amountState,
                onEvent = amountViewModel::onEvent,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    // Try popping back to Search graph first (edit from search),
                    // otherwise pop the whole AddTransaction graph
                    val poppedToSearch = navController.popBackStack(
                        route = SearchGraph.route,
                        inclusive = false
                    )
                    if (!poppedToSearch) {
                        navController.popBackStack(
                            route = AddTransactionGraph.route,
                            inclusive = true
                        )
                    }
                }
            )
        }

        // ── Inline Category Selection ──────────────────────────────────────
        // Items and pre-selection derived directly from shared AddTransactionViewModel state.
        // onConfirm fires UpdateSelectedCategory directly — no savedStateHandle round-trip.
        composable(
            route = AddTxCategorySelection.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AddTransactionGraph.route)
            }
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(graphEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()

            val itemSelectionViewModel: ItemSelectionViewModel = hiltViewModel(backStackEntry)
            val itemSelectionState by itemSelectionViewModel.state.collectAsState()

            val allSelectableCategories = remember(
                addTransactionState.allCategories,
                addTransactionState.transaction.categoryId
            ) {
                addTransactionState.allCategories.map { category ->
                    SelectableItem(
                        id = category.categoryId,
                        name = category.name,
                        isSelected = category.categoryId == addTransactionState.transaction.categoryId
                    )
                }
            }

            LaunchedEffect(Unit) {
                itemSelectionViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectableCategories,
                        preSelectedItems = allSelectableCategories.filter { it.isSelected },
                        singleSelectionMode = true
                    )
                )
            }

            ItemSelectionScreen(
                state = itemSelectionState,
                onEvent = itemSelectionViewModel::onEvent,
                title = "Category selection",
                searchPlaceholder = "Search category...",
                singleSelectionMode = true,
                onConfirm = { selectedItems ->
                    selectedItems.firstOrNull()?.let { selected ->
                        addTransactionViewModel.onEvent(
                            AddTransactionEvent.UpdateSelectedCategory(selected.id)
                        )
                    }
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        // ── Inline Person Selection ────────────────────────────────────────
        // N/A option (id = "-1") maps to null personId on confirm.
        composable(
            route = AddTxPersonSelection.route,
            enterTransition = { navEnterFromRight },
            exitTransition = { navExitToRight },
            popEnterTransition = { navEnterFromRight },
            popExitTransition = { navExitToRight }
        ) { backStackEntry ->
            val graphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AddTransactionGraph.route)
            }
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel(graphEntry)
            val addTransactionState by addTransactionViewModel.state.collectAsState()

            val itemSelectionViewModel: ItemSelectionViewModel = hiltViewModel(backStackEntry)
            val itemSelectionState by itemSelectionViewModel.state.collectAsState()

            val allSelectablePersons = remember(
                addTransactionState.allPersons,
                addTransactionState.transaction.personId
            ) {
                val naOption = SelectableItem(
                    id = "-1",
                    name = "N/A",
                    isSelected = addTransactionState.transaction.personId == null
                )
                val persons = addTransactionState.allPersons.map { person ->
                    SelectableItem(
                        id = person.personId,
                        name = person.name,
                        isSelected = person.personId == addTransactionState.transaction.personId
                    )
                }
                listOf(naOption) + persons
            }

            LaunchedEffect(Unit) {
                itemSelectionViewModel.onEvent(
                    ItemSelectionEvent.InitializeScreen(
                        allItems = allSelectablePersons,
                        preSelectedItems = allSelectablePersons.filter { it.isSelected },
                        singleSelectionMode = true
                    )
                )
            }

            ItemSelectionScreen(
                state = itemSelectionState,
                onEvent = itemSelectionViewModel::onEvent,
                title = "Person selection",
                searchPlaceholder = "Search person...",
                singleSelectionMode = true,
                onConfirm = { selectedItems ->
                    val selectedId = selectedItems.firstOrNull()?.id
                    // "-1" maps to the N/A option → null personId
                    addTransactionViewModel.onEvent(
                        AddTransactionEvent.UpdateSelectedPerson(
                            if (selectedId == "-1") null else selectedId
                        )
                    )
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
    // ── End AddTransaction nested graph ──────────────────────────────────────

    // ── EditAmount standalone composable ─────────────────────────────────────
    // Used when navigating from TransactionDetailsScreen (outside AddTransactionGraph).
    // Decodes the transaction from nav args and seeds AmountViewModel directly
    // since this path has no access to the shared AddTransactionViewModel.
    composable(
        route = EditAmount.route,
        enterTransition = { navFadeIn },
        exitTransition = { navFadeOut }
    ) { backStackEntry ->
        val amountViewModel: AmountViewModel = hiltViewModel(backStackEntry)
        val amountState by amountViewModel.state.collectAsState()

        val transactionJson = remember(backStackEntry) {
            val encoded = backStackEntry.arguments?.getString("transaction")
            Uri.decode(encoded ?: "")
        }

        LaunchedEffect(transactionJson) {
            if (transactionJson.isNotEmpty()) {
                val transaction = Gson().fromJson(transactionJson, Transaction::class.java)
                amountViewModel.loadTransaction(transaction, editMode = true)
            }
        }

        AmountScreen(
            state = amountState,
            onEvent = amountViewModel::onEvent,
            onBack = { navController.popBackStack() },
            onSuccess = { navController.popBackStack() }
        )
    }
    // ── End EditAmount ────────────────────────────────────────────────────────
}

