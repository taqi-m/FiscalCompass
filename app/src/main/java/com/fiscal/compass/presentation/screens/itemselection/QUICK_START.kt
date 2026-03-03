package com.fiscal.compass.presentation.screens.itemselection

/**
 * QUICK INTEGRATION EXAMPLE
 *
 * This file shows a minimal example of how to add ItemSelectionScreen
 * to your existing JobsScreen with just a few simple steps.
 */

/*
 * ============================================================
 * STEP 1: Update JobsScreenState.kt
 * ============================================================
 * Add selected items to your state:
 */

/*
data class JobsScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // ADD THIS:
    val selectedCategories: List<String> = emptyList()
)
*/

/*
 * ============================================================
 * STEP 2: Update JobsEvent.kt
 * ============================================================
 * Add an event to update selected items:
 */

/*
sealed class JobsEvent {
    data class SampleEventWithParameter(val value: String) : JobsEvent()
    object SampleEvent : JobsEvent()
    // ADD THIS:
    data class UpdateSelectedCategories(val categories: List<String>) : JobsEvent()
}
*/

/*
 * ============================================================
 * STEP 3: Update JobsViewModel.kt
 * ============================================================
 * Handle the new event:
 */

/*
fun onEvent(event: JobsEvent) {
    when (event) {
        JobsEvent.SampleEvent -> TODO()
        is JobsEvent.SampleEventWithParameter -> TODO()
        // ADD THIS:
        is JobsEvent.UpdateSelectedCategories -> {
            updateState { copy(selectedCategories = event.categories) }
        }
    }
}
*/

/*
 * ============================================================
 * STEP 4: Update JobsScreen.kt
 * ============================================================
 * Add a button to open selection screen and display selected items:
 */

/*
@Composable
fun JobsScreen(
    state: JobsScreenState,
    onEvent: (JobsEvent) -> Unit,
    navController: NavController // ADD this parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jobs Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ADD THIS: Show selected categories as chips
        if (state.selectedCategories.isNotEmpty()) {
            ChipFlow(
                chips = state.selectedCategories,
                onChipClick = { }, // Read-only
                chipToLabel = { it }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ADD THIS: Button to open selection screen
        Button(onClick = {
            navController.navigate("item_selection")
        }) {
            Text("Select Categories")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        if (state.error != null) {
            Text(
                text = state.error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        // ADD THIS: Listen for results from selection screen
        LaunchedEffect(navController) {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getStateFlow<List<String>?>("selected_items", null)
                ?.collect { selectedItems ->
                    selectedItems?.let {
                        onEvent(JobsEvent.UpdateSelectedCategories(it))
                        // Clear the result after reading
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<List<String>>("selected_items")
                    }
                }
        }
    }
}
*/

/*
 * ============================================================
 * STEP 5: Add Navigation Route
 * ============================================================
 * In your NavHost (usually in MainActivity or a Navigation file):
 */

/*
NavHost(navController = navController, startDestination = "jobs") {
    composable("jobs") {
        val viewModel: JobsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        JobsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            navController = navController
        )
    }

    // ADD THIS: ItemSelectionScreen route
    composable("item_selection") {
        val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
        val state by viewModel.state.collectAsState()

        // Define your categories
        val allCategories = remember {
            listOf(
                "Technology",
                "Healthcare",
                "Education",
                "Finance",
                "Construction",
                "Retail",
                "Hospitality",
                "Manufacturing",
                "Transportation",
                "Agriculture"
            )
        }

        // Get pre-selected categories from JobsScreen
        val navBackStackEntry = navController.currentBackStackEntry
        val previousEntry = navController.previousBackStackEntry

        // Initialize the screen
        LaunchedEffect(Unit) {
            viewModel.onEvent(
                ItemSelectionEvent.InitializeScreen(
                    allItems = allCategories,
                    preSelectedItems = emptyList() // or get from somewhere
                )
            )
        }

        ItemSelectionScreen(
            state = state,
            onEvent = viewModel::onEvent,
            itemToLabel = { it }, // For strings, just return the string
            title = "Select Job Categories",
            onConfirm = { selectedCategories ->
                // Pass results back to JobsScreen
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selected_items",
                    selectedCategories
                )
                navController.popBackStack()
            },
            onCancel = {
                navController.popBackStack()
            },
            searchPlaceholder = "Search categories..."
        )
    }
}
*/

/*
 * ============================================================
 * STEP 6: Add Required Imports
 * ============================================================
 * Make sure you have these imports in JobsScreen.kt:
 */

/*
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import com.app.uniqueplant.ui.components.cards.ChipFlow
import androidx.compose.material3.Button
*/

/*
 * ============================================================
 * THAT'S IT!
 * ============================================================
 *
 * Your JobsScreen now has a working category selector using
 * the new ItemSelectionScreen.
 *
 * To customize further:
 * - Change "Technology", "Healthcare", etc. to your actual categories
 * - Load categories from a database or API
 * - Store selections in a repository for persistence
 * - Use a SharedViewModel for complex shared state
 *
 * For more advanced examples, see USAGE_EXAMPLE.kt
 */

