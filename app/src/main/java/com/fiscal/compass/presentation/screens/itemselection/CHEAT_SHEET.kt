/*
*
 * ═══════════════════════════════════════════════════════════════════════
 * ITEM SELECTION SCREEN - QUICK REFERENCE CHEAT SHEET
 * ═══════════════════════════════════════════════════════════════════════

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 1. BASIC USAGE (Strings)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
val state by viewModel.state.collectAsState()

LaunchedEffect(Unit) {
    viewModel.onEvent(
        ItemSelectionEvent.InitializeScreen(
            allItems = listOf("Item1", "Item2", "Item3"),
            preSelectedItems = listOf("Item1")
        )
    )
}

ItemSelectionScreen(
    state = state,
    onEvent = viewModel::onEvent,
    itemToLabel = { it },
    title = "Select Items",
    onConfirm = { items -> navController.popBackStack() },
    onCancel = { navController.popBackStack() }
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 2. CUSTOM DATA CLASS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

data class Item(val id: String, val name: String) {
    override fun equals(other: Any?) = (other as? Item)?.id == id
    override fun hashCode() = id.hashCode()
}

val viewModel: ItemSelectionViewModel<Item> = hiltViewModel()
// ... initialize ...
ItemSelectionScreen(
    state = state,
    onEvent = viewModel::onEvent,
    itemToLabel = { it.name }, // ← Show name in UI
    title = "Select",
    onConfirm = { items ->  ...  },
    onCancel = {  ...  }
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 3. NAVIGATION ROUTE
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

NavHost(...) {
    composable("item_selection") {
        val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onEvent(
                ItemSelectionEvent.InitializeScreen(
                    allItems = listOf("A", "B", "C"),
                    preSelectedItems = emptyList()
                )
            )
        }

        ItemSelectionScreen(
            state = state,
            onEvent = viewModel::onEvent,
            itemToLabel = { it },
            title = "Select",
            onConfirm = { selected ->
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selected_items", selected
                )
                navController.popBackStack()
            },
            onCancel = { navController.popBackStack() }
        )
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 4. NAVIGATE TO SCREEN
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Button(onClick = { navController.navigate("item_selection") }) {
    Text("Open Selection")
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 5. RECEIVE RESULTS IN PARENT
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

LaunchedEffect(navController) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<List<String>?>("selected_items", null)
        ?.collect { items ->
            items?.let {
                // Handle results here
                println("Selected: $it")
                // Clear after reading
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<List<String>>("selected_items")
            }
        }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 6. ALL EVENTS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

viewModel.onEvent(ItemSelectionEvent.SearchQueryChanged("query"))
viewModel.onEvent(ItemSelectionEvent.ItemToggled(item))
viewModel.onEvent(ItemSelectionEvent.ConfirmSelection)
viewModel.onEvent(ItemSelectionEvent.CancelSelection)
viewModel.onEvent(ItemSelectionEvent.InitializeScreen(allItems, preSelected))

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 7. STATE PROPERTIES
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

state.searchQuery          // Current search text
state.allItems             // All available items
state.selectedItems        // Selected items (Set<T>)
state.isLoading           // Loading state
state.error               // Error message
state.getFilteredItems()  // Filtered items based on search

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 8. PARAMETERS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ItemSelectionScreen(
    state: ItemSelectionScreenState<T>,           // Required
    onEvent: (ItemSelectionEvent<T>) -> Unit,     // Required
    itemToLabel: (T) -> String,                   // Required - How to display items
    title: String,                                // Required - Screen title
    onConfirm: (List<T>) -> Unit,                 // Required - Handle selection
    onCancel: () -> Unit,                         // Required - Handle cancel
    modifier: Modifier = Modifier,                // Optional
    searchPlaceholder: String = "Search..."       // Optional
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 9. COMMON PATTERNS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// Get selected items from ViewModel
val selected: List<T> = viewModel.getSelectedItems()

// Initialize with data
viewModel.onEvent(
    ItemSelectionEvent.InitializeScreen(
        allItems = repository.getAllItems(),
        preSelectedItems = currentState.selectedItems
    )
)

// Update search
viewModel.onEvent(ItemSelectionEvent.SearchQueryChanged(query))

// Toggle item
viewModel.onEvent(ItemSelectionEvent.ItemToggled(item))

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 10. IMPORTANT NOTES
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// ✅ Data classes MUST implement equals/hashCode for Set operations
// ✅ Always initialize screen with InitializeScreen event
// ✅ Navigation is handled by parent, not ViewModel
// ✅ Use SavedStateHandle or SharedViewModel to pass data
// ✅ Clear SavedStateHandle after reading to prevent re-triggering
// ✅ Screen uses Set<T> internally for O(1) selection checks
// ✅ Filtering is case-insensitive and uses contains()
// ✅ itemToLabel determines both display AND search matching

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 11. TROUBLESHOOTING
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// Problem: Items not selecting
// Solution: Implement proper equals/hashCode in your data class

// Problem: Generic type error
// Solution: Specify type when getting ViewModel
//   val viewModel: ItemSelectionViewModel<YourType> = hiltViewModel()

// Problem: Search not working
// Solution: Check itemToLabel returns correct string to search

// Problem: State not persisting
// Solution: Use ViewModel (hiltViewModel), not remember

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 12. PERFORMANCE TIPS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// ✅ Use derivedStateOf for filtering (already done internally)
// ✅ LazyColumn for large lists (already done)
// ✅ Stable keys for items (already done with itemToLabel)
// ✅ Set<T> for O(1) selection checks (already done)
// ✅ Minimal recomposition scope (already optimized)

*
 * ═══════════════════════════════════════════════════════════════════════
 * For detailed examples, see:
 * - README.md (full documentation)
 * - USAGE_EXAMPLE.kt (advanced patterns)
 * - QUICK_START.kt (step-by-step integration)
 * ═══════════════════════════════════════════════════════════════════════

*/
