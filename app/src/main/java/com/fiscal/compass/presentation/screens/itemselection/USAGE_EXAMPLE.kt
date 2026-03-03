package com.fiscal.compass.presentation.screens.itemselection

/**
 * USAGE EXAMPLE: How to integrate ItemSelectionScreen into your navigation
 *
 * This file demonstrates the complete integration pattern for the ItemSelectionScreen
 * following the navigation approach used in the app.
 */

/*
 * ========================================================================
 * STEP 1: Define Navigation Route
 * ========================================================================
 * Add to your navigation constants/sealed class:
 */

// Example route definition
// object ItemSelectionRoute : Route {
//     const val ROUTE = "item_selection/{title}"
// }

/*
 * ========================================================================
 * STEP 2: Add to Navigation Graph
 * ========================================================================
 * In your NavHost setup:
 */

// Example navigation graph setup:
/*
composable(
    route = "item_selection/{title}",
    arguments = listOf(
        navArgument("title") { type = NavType.StringType }
    )
) { backStackEntry ->
    val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val title = backStackEntry.arguments?.getString("title") ?: "Select Items"

    // Initialize screen with data (could come from navigation arguments or parent state)
    LaunchedEffect(Unit) {
        val allItems = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
        val preSelectedItems = listOf("Item 2", "Item 4")
        viewModel.onEvent(
            ItemSelectionEvent.InitializeScreen(
                allItems = allItems,
                preSelectedItems = preSelectedItems
            )
        )
    }

    ItemSelectionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        itemToLabel = { it }, // For String type, just return itself
        title = title,
        onConfirm = { selectedItems ->
            // Handle the selected items
            // Option 1: Save to SavedStateHandle for retrieval
            // Option 2: Use SharedViewModel
            // Option 3: Use navigation result callback
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "selected_items",
                selectedItems
            )
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        }
    )
}
*/

/*
 * ========================================================================
 * STEP 3: Navigate to Screen from Parent
 * ========================================================================
 * From the parent screen that needs item selection:
 */

// Example: Navigate to selection screen
/*
Button(onClick = {
    navController.navigate("item_selection/Select Fruits")
}) {
    Text("Select Items")
}

// Retrieve results when returning
LaunchedEffect(navController) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<List<String>?>("selected_items", null)
        ?.collect { selectedItems ->
            selectedItems?.let {
                // Handle the returned selected items
                println("Selected: $it")
                // Clear the result
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<List<String>>("selected_items")
            }
        }
}
*/

/*
 * ========================================================================
 * STEP 4: Example with Custom Data Class
 * ========================================================================
 * For using with custom data classes instead of String:
 */

// Example data class
/*
data class Plant(
    val id: String,
    val name: String,
    val scientificName: String
) {
    // Important: Implement proper equals/hashCode for Set operations
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Plant
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

// Usage in navigation:
composable("select_plants") {
    val viewModel: ItemSelectionViewModel<Plant> = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val allPlants = remember {
        listOf(
            Plant("1", "Rose", "Rosa rubiginosa"),
            Plant("2", "Tulip", "Tulipa gesneriana"),
            Plant("3", "Lily", "Lilium candidum")
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            ItemSelectionEvent.InitializeScreen(
                allItems = allPlants,
                preSelectedItems = emptyList()
            )
        )
    }

    ItemSelectionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        itemToLabel = { it.name }, // Show plant name in UI
        title = "Select Plants",
        onConfirm = { selectedPlants ->
            // Handle selected plants
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "selected_plants",
                selectedPlants.map { it.id } // Store only IDs
            )
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
        searchPlaceholder = "Search plants..."
    )
}
*/

/*
 * ========================================================================
 * STEP 5: Alternative - Using SharedViewModel Pattern
 * ========================================================================
 * If you prefer using a shared ViewModel between parent and selection screen:
 */

/*
// In parent screen
@Composable
fun ParentScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    Button(onClick = {
        // Set data in shared ViewModel before navigating
        sharedViewModel.setItemsForSelection(
            allItems = listOf("A", "B", "C"),
            preSelected = listOf("A")
        )
        navController.navigate("item_selection")
    }) {
        Text("Select Items")
    }

    // Observe selected items from shared ViewModel
    val selectedItems by sharedViewModel.selectedItems.collectAsState()
}

// In ItemSelectionScreen composable
@Composable
fun ItemSelectionScreenRoute(
    navController: NavController,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val selectionData by sharedViewModel.selectionData.collectAsState()

    LaunchedEffect(selectionData) {
        selectionData?.let { data ->
            viewModel.onEvent(
                ItemSelectionEvent.InitializeScreen(
                    allItems = data.allItems,
                    preSelectedItems = data.preSelected
                )
            )
        }
    }

    ItemSelectionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        itemToLabel = { it },
        title = "Select Items",
        onConfirm = { selectedItems ->
            sharedViewModel.updateSelectedItems(selectedItems)
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        }
    )
}
*/

/*
 * ========================================================================
 * STEP 6: Complete Example - JobsScreen Integration
 * ========================================================================
 * Example of how JobsScreen could use ItemSelectionScreen:
 */

/*
// In JobsScreen.kt
@Composable
fun JobsScreen(
    state: JobsScreenState,
    onEvent: (JobsEvent) -> Unit,
    navController: NavController
) {
    // ... existing code ...

    // Add a button to open selection screen
    Button(onClick = {
        navController.navigate("item_selection/Select Job Categories")
    }) {
        Text("Select Categories")
    }

    // Retrieve selected categories when returning
    LaunchedEffect(navController) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<List<String>?>("selected_items", null)
            ?.collect { selectedCategories ->
                selectedCategories?.let {
                    onEvent(JobsEvent.UpdateCategories(it))
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<List<String>>("selected_items")
                }
            }
    }
}

// In JobsEvent.kt - Add new event
sealed class JobsEvent {
    data class UpdateCategories(val categories: List<String>) : JobsEvent()
    // ... existing events ...
}

// In JobsScreenState.kt - Add field
data class JobsScreenState(
    val selectedCategories: List<String> = emptyList(),
    // ... existing fields ...
)
*/

/*
 * ========================================================================
 * NOTES AND BEST PRACTICES
 * ========================================================================
 *
 * 1. Type Safety: Ensure your data class implements proper equals/hashCode
 *    for Set operations to work correctly
 *
 * 2. Navigation: Consider using type-safe navigation with Navigation Compose
 *    for better compile-time safety
 *
 * 3. State Persistence: Selected items are maintained during configuration
 *    changes thanks to ViewModel
 *
 * 4. Performance: The screen uses LazyColumn and derivedStateOf for
 *    efficient rendering and filtering
 *
 * 5. Hilt: Remember to add @HiltViewModel annotation is already included
 *    and @HiltAndroidApp in your Application class
 *
 * 6. Generic Constraints: The <T : Any> constraint ensures non-nullable types
 *    and proper behavior with Set operations
 *
 * 7. Search Performance: Filtering is case-insensitive and happens in
 *    the state layer with derivedStateOf to prevent unnecessary recompositions
 */

