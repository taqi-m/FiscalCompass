package com.app.uniqueplant.presentation.admin.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.presentation.mappers.toCategory
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.ui.components.buttons.AddNewButton
import com.app.uniqueplant.ui.components.cards.ExpandableChipCard
import com.app.uniqueplant.ui.components.dialogs.AddCategoryDialog
import com.app.uniqueplant.ui.components.dialogs.DeleteCategoryDialog
import com.app.uniqueplant.ui.components.dialogs.EditCategoryDialog
import com.app.uniqueplant.ui.components.input.TransactionTypeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    state: CategoriesScreenState,
    onEvent: (CategoriesEvent) -> Unit,
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState = state.uiState

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = "Categories") },
                actions = {
                    Button(
                        onClick = {
                            onEvent(CategoriesEvent.OnCategoryDialogToggle(CategoryDialogToggle.Add()))
                        }, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Add New")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        },
    )
    { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        )
        {
            LaunchedEffect(uiState) {
                val message: String? = when (uiState) {
                    is UiState.Error -> uiState.message
                    is UiState.Success -> uiState.message
                    else -> null
                }
                message?.let { msg ->
                    if (msg.isNotEmpty()) {
                        snackBarHostState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Dismiss"
                        )
                        onEvent(CategoriesEvent.OnUiReset)
                    }
                }
            }

            CategoriesScreenContent(
                state = state, onEvent = onEvent, modifier = Modifier.fillMaxSize()
            )

            CategoryScreenDialogs(
                currentDialog = state.currentDialog,
                dialogState = state.dialogState,
                onDialogSubmit = { submittedDialog ->
                    onEvent(
                        CategoriesEvent.OnCategoryDialogSubmit(
                            submittedDialog
                        )
                    )
                },
                onDialogDismiss = { dialogToggled ->
                    onEvent(
                        CategoriesEvent.OnCategoryDialogToggle(
                            dialogToggled
                        )
                    )
                }
            )
        }
    }
}


@Composable
fun CategoriesScreenContent(
    state: CategoriesScreenState, onEvent: (CategoriesEvent) -> Unit, modifier: Modifier = Modifier
) {
    CategoriesList(
        modifier = modifier.fillMaxSize(),
        uiState = state.uiState,
        transactionType = state.transactionType,
        categoriesMap = state.categories,
        onEditCategoryClicked = { category ->
            onEvent(
                CategoriesEvent.OnCategoryDialogToggle(
                    CategoryDialogToggle.Edit(category)
                )
            )
        },
        onDeleteCategoryClicked = { category ->
            onEvent(
                CategoriesEvent.OnCategoryDialogToggle(
                    CategoryDialogToggle.Delete(category)
                )
            )
        },
        onAddNewCategoryClicked = { categoryGroup ->
            onEvent(
                CategoriesEvent.OnCategoryDialogToggle(
                    CategoryDialogToggle.Add(categoryGroup)
                )
            )
        },
        onTransactionTypeChange = { type ->
            onEvent(
                CategoriesEvent.OnTransactionTypeChange(type)
            )
        })
}


@Composable
fun CategoriesList(
    modifier: Modifier = Modifier,
    uiState: UiState,
    transactionType: TransactionType,
    categoriesMap: GroupedCategoryUi,
    onEditCategoryClicked: (CategoryUi) -> Unit,
    onDeleteCategoryClicked: (CategoryUi) -> Unit,
    onAddNewCategoryClicked: (Long?) -> Unit,
    onTransactionTypeChange: (TransactionType) -> Unit,
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth()
    )
    {
        stickyHeader {
            TransactionTypeSelector(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                selectedOption = transactionType,
                onOptionSelected = { type ->
                    onTransactionTypeChange(type)
                })
        }
        when (uiState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading categories...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            else -> {
                categoriesMap.forEach { (category, categories) ->
                    item {
                        ExpandableChipCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            title = category.name,
                            trailingIcon = {
                                FilledTonalIconButton (
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    shape = RoundedCornerShape(15),
                                    onClick = {
                                        onAddNewCategoryClicked(category.categoryId)
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.height(24.dp),
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Filter"
                                    )
                                }
                            },
                            chips = categories,
                            onChipClick = { category ->
                                onEditCategoryClicked(category)
                            },
                            initiallyExpanded = true,
                            chipToLabel = { it.name }
                        )
                    }
                }

                item {
                    AddNewButton(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            ), text = "Add New Category",
                        onClick = {
                            onAddNewCategoryClicked(null)
                        })
                }
            }
        }
    }
}

@Composable
fun CategoryScreenDialogs(
    currentDialog: CategoriesDialog,
    dialogState: CategoryDialogState,
    onDialogSubmit: (CategoryDialogSubmit) -> Unit,
    onDialogDismiss: (CategoryDialogToggle) -> Unit,
) {
    when (currentDialog) {
        is CategoriesDialog.AddCategory -> {
            AddCategoryDialog(
                onAddNewCategory = { name, description, expectedPersonType ->
                    onDialogSubmit(
                        CategoryDialogSubmit.Add(
                            parentId = dialogState.parentId,
                            name = name,
                            description = description,
                            expectedPersonType = expectedPersonType
                        )
                    )
                }, onDismiss = {
                    onDialogDismiss(CategoryDialogToggle.Hidden)
                })
        }

        is CategoriesDialog.EditCategory -> {
            EditCategoryDialog(
                category = dialogState.category?.toCategory(),
                onEditCategory = { category ->
                    onDialogSubmit(
                        CategoryDialogSubmit.Edit(
                            category = category
                        )
                    )
                },
                onDismiss = {
                    onDialogDismiss(CategoryDialogToggle.Hidden)
                })
        }

        is CategoriesDialog.DeleteCategory -> {
            DeleteCategoryDialog(
                categoryName = dialogState.category?.name,
                onDeleteConfirm = {
                    onDialogSubmit(
                        CategoryDialogSubmit.Delete
                    )
                },
                onDismissRequest = {
                    onDialogDismiss(CategoryDialogToggle.Hidden)
                })
        }

        else -> {}
    }
}
