# AddTransactionScreen Selection Field Update Bug Fix

## Summary
Fixed critical UI update delay issues where selected Category and Person values were not reflecting instantly in SelectionField components, causing a one-composition lag.

## Issues Identified and Fixed

### Issue 1: SelectionField Values Not Updating Instantly

**Problem:**
- Category and Person SelectionFields showed values from the previous composition
- When Category was selected, it wouldn't display immediately
- When Person was selected, Category would finally show but Person wouldn't
- This created a cascading delay effect

**Root Cause:**
The code was using `var` with `mutableStateOf` inside the composable, combined with `LaunchedEffect` to derive values:

```kotlin
var selectedCategory by remember { mutableStateOf<Category?>(null) }

LaunchedEffect(state.allCategories) {
    if (state.allCategories.isEmpty())
        return@LaunchedEffect
    val newCategory = state.allCategories.firstOrNull { it.categoryId == state.transaction.categoryId }
    if (newCategory == null)
        return@LaunchedEffect
    selectedCategory = newCategory
}
```

This approach caused:
1. State updates in ViewModel
2. Recomposition triggered
3. `LaunchedEffect` scheduled to run after composition
4. Local variable updated in next frame
5. Another recomposition needed to show the update

**Solution:**
Use `remember` with proper keys to derive values directly from state:

```kotlin
// Derive selected category directly from state - instant update
val selectedCategory = remember(state.transaction.categoryId, state.allCategories) {
    state.allCategories.firstOrNull { it.categoryId == state.transaction.categoryId }
}

// Derive selected person directly from state - instant update
val selectedPerson = remember(state.transaction.personId, state.allPersons) {
    state.allPersons.firstOrNull { it.personId == state.transaction.personId }
}
```

Benefits:
- Values are computed immediately during composition
- No additional recomposition needed
- Updates happen instantly when state changes
- Keys (`state.transaction.categoryId`, `state.allCategories`) ensure recalculation when needed

### Issue 2: SnackBar Showing After Next Composition

**Problem:**
SnackBar error messages (e.g., "No categories found") were delayed and showed after the next composition.

**Root Cause:**
UI state updates were being made with direct assignment instead of using the `updateState` helper:

```kotlin
_state.value = _state.value.copy(uiState = UiState.Error("No categories found"))
```

**Solution:**
Use the `updateState` helper consistently for all state updates:

```kotlin
updateState {
    copy(uiState = UiState.Error("No categories found"))
}
```

### Issue 3: assignCategories Performance

**Problem:**
`assignCategories()` was launching a new coroutine on `Dispatchers.IO` for simple state updates, causing unnecessary threading overhead and potential delays.

**Root Cause:**
```kotlin
private fun assignCategories() {
    viewModelScope.launch(Dispatchers.IO) {
        // Simple state updates don't need IO dispatcher
        val transactionType = TransactionType.fromString(_state.value.transaction.transactionType)
        // ... state updates
    }
}
```

**Solution:**
Remove the unnecessary coroutine launch since no IO operations are performed:

```kotlin
private fun assignCategories() {
    val transactionType = TransactionType.fromString(_state.value.transaction.transactionType)
    when (transactionType) {
        TransactionType.EXPENSE -> {
            val updatedTransaction = _state.value.transaction.copy(categoryId = 0)
            updateState {
                copy(
                    allCategories = expenseCategories,
                    transaction = updatedTransaction
                )
            }
        }
        // ...
    }
}
```

## Files Modified

### 1. AddTransactionScreen.kt
- Removed `var selectedCategory` and `var selectedPerson` with `LaunchedEffect`
- Changed to `val` with `remember(keys)` for instant derivation
- Removed unused imports: `Category`, `Person`, `PersonProvider`

**Lines changed:** ~255-310

### 2. AddTransactionViewModel.kt
- Updated `NavigateToCategorySelection` event to use `updateState` helper
- Simplified `assignCategories()` by removing unnecessary coroutine launch
- Ensured all UI state updates happen synchronously on Main dispatcher

**Lines changed:** 125-135, 192-213

## Benefits

1. **Instant UI Updates**: Category and Person selections now reflect immediately
2. **Better Performance**: Removed unnecessary coroutine launches and threading overhead
3. **Consistent State Management**: All state updates use the same pattern (`updateState`)
4. **Predictable Behavior**: No more composition lag or cascading delays
5. **Cleaner Code**: Simpler logic without complex LaunchedEffect chains

## Testing Recommendations

1. **Test Category Selection:**
   - Select a category → Should display instantly
   - Switch transaction type → Category should reset immediately
   - Select another category → Should update instantly

2. **Test Person Selection:**
   - Select a person → Should display instantly
   - Select "N/A" → Should clear instantly
   - Select different persons in sequence → Each should update instantly

3. **Test Error Messages:**
   - Try to select category when none exist → SnackBar should show immediately
   - Switch transaction types with empty categories → Error should show instantly

4. **Test Edge Cases:**
   - Rapid selection changes → Should always show the latest selection
   - Switch transaction type while on Person selection → Should work smoothly
   - Navigate back from selection screens → Previous values should be retained

## Key Learning: Compose State Derivation

**❌ Don't do this (causes delay):**
```kotlin
var derivedValue by remember { mutableStateOf(...) }
LaunchedEffect(sourceState) {
    derivedValue = computeFromState(sourceState)
}
```

**✅ Do this instead (instant update):**
```kotlin
val derivedValue = remember(sourceState) {
    computeFromState(sourceState)
}
```

## Date
December 23, 2025
