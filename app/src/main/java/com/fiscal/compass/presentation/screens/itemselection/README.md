# ItemSelectionScreen

A generic, reusable full-screen item selection component for Jetpack Compose following MVVM architecture pattern.

## Overview

`ItemSelectionScreen` is a type-safe, generic implementation that allows users to search and select multiple items from a list. It's built to replace inline chip selectors with a full-screen experience, following the same architecture pattern as other screens in the app (like `JobsScreen`).

## Features

✅ **Generic Implementation** - Works with any data type `<T : Any>`  
✅ **MVVM Architecture** - State, Event, ViewModel, Screen separation  
✅ **Search Functionality** - Real-time case-insensitive filtering  
✅ **Multi-selection** - Select/deselect multiple items with checkboxes  
✅ **Material Design 3** - Follows Material 3 design guidelines  
✅ **Performance Optimized** - Uses `LazyColumn`, `derivedStateOf`, and stable keys  
✅ **State Persistence** - Survives configuration changes via ViewModel  
✅ **Hilt Integration** - Ready for dependency injection  
✅ **Type Safe** - Full compile-time type safety with generics  

## File Structure

```
com.app.uniqueplant.presentation.screens.itemselection/
├── ItemSelectionScreenState.kt   # State data class with filtering logic
├── ItemSelectionEvent.kt          # Sealed class for all user events
├── ItemSelectionViewModel.kt      # ViewModel managing state and business logic
├── ItemSelectionScreen.kt         # Main composable UI
├── USAGE_EXAMPLE.kt              # Comprehensive integration examples
└── README.md                     # This file
```

## Components

### 1. ItemSelectionScreenState

```kotlin
data class ItemSelectionScreenState<T : Any>(
    val searchQuery: String = "",
    val allItems: List<T> = emptyList(),
    val selectedItems: Set<T> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Features:**
- Generic type parameter `<T : Any>` for any non-nullable type
- Uses `Set<T>` for efficient O(1) selection lookups
- `getFilteredItems()` method for computed filtering based on search query
- Standard loading and error states

### 2. ItemSelectionEvent

```kotlin
sealed class ItemSelectionEvent<out T : Any> {
    data class SearchQueryChanged(val query: String) : ItemSelectionEvent<Nothing>()
    data class ItemToggled<T : Any>(val item: T) : ItemSelectionEvent<T>()
    data object ConfirmSelection : ItemSelectionEvent<Nothing>()
    data object CancelSelection : ItemSelectionEvent<Nothing>()
    data class InitializeScreen<T : Any>(
        val allItems: List<T>,
        val preSelectedItems: List<T>
    ) : ItemSelectionEvent<T>()
}
```

**Events:**
- `SearchQueryChanged` - User types in search field
- `ItemToggled` - User selects/deselects an item
- `ConfirmSelection` - User confirms selection (navigation handled by parent)
- `CancelSelection` - User cancels (navigation handled by parent)
- `InitializeScreen` - Sets up initial data when screen opens

### 3. ItemSelectionViewModel

```kotlin
@HiltViewModel
class ItemSelectionViewModel<T : Any> @Inject constructor() : ViewModel() {
    val state: StateFlow<ItemSelectionScreenState<T>>
    fun onEvent(event: ItemSelectionEvent<T>)
    fun getSelectedItems(): List<T>
}
```

**Features:**
- Hilt-ready with `@HiltViewModel` annotation
- Exposes `StateFlow` for reactive UI updates
- `onEvent()` handles all user interactions
- `getSelectedItems()` provides current selection as a List
- No navigation logic (handled by parent/caller)

### 4. ItemSelectionScreen (Composable)

```kotlin
@Composable
fun <T : Any> ItemSelectionScreen(
    state: ItemSelectionScreenState<T>,
    onEvent: (ItemSelectionEvent<T>) -> Unit,
    itemToLabel: (T) -> String,
    title: String,
    onConfirm: (List<T>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search..."
)
```

**UI Components:**
- **TopAppBar** with title, back button, and confirm button
- **Search TextField** for filtering items
- **Selection Counter** showing "X items selected"
- **LazyColumn** with selectable items (Checkbox + Text)
- **Empty States** for no items available / no search results
- **Loading & Error States** support

## Quick Start

### Basic Example with Strings

```kotlin
@Composable
fun MyScreen(navController: NavController) {
    val viewModel: ItemSelectionViewModel<String> = hiltViewModel()
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.onEvent(
            ItemSelectionEvent.InitializeScreen(
                allItems = listOf("Apple", "Banana", "Cherry"),
                preSelectedItems = listOf("Banana")
            )
        )
    }
    
    ItemSelectionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        itemToLabel = { it },
        title = "Select Fruits",
        onConfirm = { selectedItems ->
            // Handle selection
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        }
    )
}
```

### Advanced Example with Custom Data Class

```kotlin
data class Category(
    val id: String,
    val name: String,
    val description: String
) {
    // Important: Implement equals/hashCode for Set operations
    override fun equals(other: Any?) = (other as? Category)?.id == id
    override fun hashCode() = id.hashCode()
}

@Composable
fun CategorySelectionScreen(navController: NavController) {
    val viewModel: ItemSelectionViewModel<Category> = hiltViewModel()
    val state by viewModel.state.collectAsState()
    
    val allCategories = remember {
        listOf(
            Category("1", "Technology", "Tech related jobs"),
            Category("2", "Healthcare", "Medical positions"),
            Category("3", "Education", "Teaching roles")
        )
    }
    
    LaunchedEffect(Unit) {
        viewModel.onEvent(
            ItemSelectionEvent.InitializeScreen(
                allItems = allCategories,
                preSelectedItems = emptyList()
            )
        )
    }
    
    ItemSelectionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        itemToLabel = { it.name }, // Display name in UI
        title = "Select Categories",
        onConfirm = { selected ->
            // selected is List<Category>
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "selected_categories",
                selected.map { it.id }
            )
            navController.popBackStack()
        },
        onCancel = {
            navController.popBackStack()
        },
        searchPlaceholder = "Search categories..."
    )
}
```

## Navigation Integration

### Option 1: SavedStateHandle (Recommended)

**Navigate to selection screen:**
```kotlin
Button(onClick = { navController.navigate("item_selection") }) {
    Text("Select Items")
}
```

**Retrieve results in parent:**
```kotlin
LaunchedEffect(navController) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<List<String>?>("selected_items", null)
        ?.collect { selectedItems ->
            selectedItems?.let {
                // Handle results
                // Clear after reading
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<List<String>>("selected_items")
            }
        }
}
```

### Option 2: SharedViewModel

```kotlin
@HiltViewModel
class SharedDataViewModel @Inject constructor() : ViewModel() {
    private val _selectedItems = MutableStateFlow<List<String>>(emptyList())
    val selectedItems = _selectedItems.asStateFlow()
    
    fun updateSelection(items: List<String>) {
        _selectedItems.value = items
    }
}

// Both screens access the same ViewModel
val sharedViewModel: SharedDataViewModel = hiltViewModel()
```

## Performance Optimizations

1. **Lazy Loading**: Uses `LazyColumn` for efficient list rendering
2. **Derived State**: `derivedStateOf` prevents unnecessary recompositions during filtering
3. **Stable Keys**: Each list item uses `itemToLabel(item)` as key for stability
4. **Set for Selection**: O(1) lookup time for checking if item is selected
5. **Minimal Recomposition**: Search field isolated to prevent full screen recomposition

## Type Requirements

For custom data classes used with this screen:

```kotlin
data class MyItem(
    val id: String,
    val name: String
) {
    // REQUIRED: Implement for Set operations
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MyItem
        return id == other.id
    }
    
    override fun hashCode(): Int = id.hashCode()
}
```

**Why?** The screen uses `Set<T>` for selected items. For Sets to work correctly, items must have proper `equals()` and `hashCode()` implementations. Data classes auto-generate these, but you may want to customize them (e.g., comparing only by `id`).

## Testing

### Preview Composables

The implementation includes 4 preview composables:

1. **ItemSelectionScreenPreview** - Normal state with items and selection
2. **ItemSelectionScreenEmptyPreview** - Empty state (no items)
3. **ItemSelectionScreenNoResultsPreview** - No search results
4. **ItemSelectionScreenLoadingPreview** - Loading state

Run previews in Android Studio to see all states.

### Unit Testing ViewModel

```kotlin
@Test
fun `test item selection`() = runTest {
    val viewModel = ItemSelectionViewModel<String>()
    
    viewModel.onEvent(
        ItemSelectionEvent.InitializeScreen(
            allItems = listOf("A", "B", "C"),
            preSelectedItems = listOf("A")
        )
    )
    
    viewModel.onEvent(ItemSelectionEvent.ItemToggled("B"))
    
    val selected = viewModel.getSelectedItems()
    assertEquals(setOf("A", "B"), selected.toSet())
}
```

## Migration from SearchableChipSelector

If you're replacing `SearchableChipSelector` with this screen:

**Before:**
```kotlin
SearchableChipSelector(
    title = "Categories",
    searchQuery = state.categorySearch,
    showDropdown = state.showCategoryDropdown,
    allItems = allCategories,
    selectedItems = state.selectedCategories,
    searchPlaceholder = "Search categories",
    itemToLabel = { it.name },
    onSearchQueryChanged = { onEvent(Event.CategorySearchChanged(it)) },
    onDropdownVisibilityChanged = { onEvent(Event.CategoryDropdownChanged(it)) },
    onItemSelected = { onEvent(Event.CategorySelected(it)) },
    onChipClicked = { onEvent(Event.CategoryRemoved(it)) }
)
```

**After:**
```kotlin
// In your main screen, show chips read-only + button to open selection
ChipFlow(
    chips = state.selectedCategories,
    onChipClick = { }, // Chips are read-only now
    chipToLabel = { it.name }
)

Button(onClick = { navController.navigate("category_selection") }) {
    Text("Select Categories")
}

// Create separate ItemSelectionScreen route in navigation
```

**Benefits:**
- Cleaner separation of concerns
- Better UX for large lists
- Easier testing
- No complex dropdown state management

## Best Practices

1. ✅ **Always initialize screen data** with `InitializeScreen` event in `LaunchedEffect(Unit)`
2. ✅ **Implement equals/hashCode** for custom data classes
3. ✅ **Use itemToLabel** to control what users see (e.g., show name but compare by id)
4. ✅ **Handle navigation in parent** (ViewModel has no navigation dependencies)
5. ✅ **Clear SavedStateHandle** after reading results to prevent re-triggering
6. ✅ **Use remember** for static data to avoid recreating lists on recomposition

## Customization

### Changing Empty State Message

```kotlin
ItemSelectionScreen(
    // ... other params
    searchPlaceholder = "Type to search items..."
)

// For "No items found", edit ItemSelectionScreen.kt line ~133:
// text = if (state.allItems.isEmpty()) {
//     "Your custom no items message"
// } else {
//     "Your custom no results message"
// }
```

### Custom Item Row Layout

Edit the `SelectableItemRow` composable in `ItemSelectionScreen.kt` to customize the appearance of each item.

### Adding Item Metadata

```kotlin
// Show subtitle or additional info
Row(
    modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onToggle)
        .padding(16.dp)
) {
    Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
    Column(modifier = Modifier.weight(1f)) {
        Text(itemToLabel(item), style = MaterialTheme.typography.bodyLarge)
        Text(
            text = item.subtitle, // Add subtitle to your model
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

## Troubleshooting

### Issue: Items not getting selected/deselected

**Solution:** Ensure your data class implements proper `equals()` and `hashCode()`. The default implementation compares all properties, which might not be what you want.

### Issue: ViewModel generic type error

**Solution:** Ensure you're specifying the type when getting the ViewModel:
```kotlin
val viewModel: ItemSelectionViewModel<YourType> = hiltViewModel()
```

### Issue: Search not working

**Solution:** Verify that `itemToLabel` returns the correct string to search against. The search is case-insensitive and uses `contains()`.

### Issue: Selected items not persisting

**Solution:** Make sure you're using ViewModel (not just remember state) and initializing with `InitializeScreen` event.

## License

Part of the Unique Plant application.

## Support

For issues or questions, please refer to the `USAGE_EXAMPLE.kt` file for comprehensive integration examples.

