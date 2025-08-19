package com.app.uniqueplant.presentation.admin.categories

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.ui.components.buttons.AddNewButton
import com.app.uniqueplant.ui.components.cards.CategoryItem
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

    val uiState = state.uiState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Categories") }, actions = {
                Button(
                    onClick = {
                        onEvent(CategoriesEvent.OnCategoryDialogToggle(CategoryDialogToggle.Add))
                    }, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "Add Category")
                }
            })
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        },
    ) { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center
        ) {
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

            when (state.currentDialog) {
                is CategoriesDialog.AddCategory -> {
                    AddCategoryDialog(onAddNewCategory = { name, description, expectedPersonType ->
                        onEvent(
                            CategoriesEvent.OnCategoryDialogSubmit(
                                CategoryDialogSubmit.Add(
                                    name, description, expectedPersonType
                                )
                            )
                        )
                    }, onDismiss = {
                        onEvent(
                            CategoriesEvent.OnCategoryDialogToggle(
                                CategoryDialogToggle.Hidden
                            )
                        )
                    })
                }

                is CategoriesDialog.EditCategory -> {
                    EditCategoryDialog(
                        category = state.dialogState.category,
                        onEditCategory = { category ->
                            onEvent(
                                CategoriesEvent.OnCategoryDialogSubmit(
                                    CategoryDialogSubmit.Edit(category)
                                )
                            )
                        },
                        onDismiss = {
                            onEvent(
                                CategoriesEvent.OnCategoryDialogToggle(
                                    CategoryDialogToggle.Hidden
                                )
                            )
                        })
                }

                is CategoriesDialog.DeleteCategory -> {
                    Log.d(
                        "CategoriesScreen",
                        "DeleteCategoryDialog: ${state.dialogState.category?.name}"
                    )
                    DeleteCategoryDialog(
                        categoryName = state.dialogState.category?.name,
                        onDeleteConfirm = {
                            onEvent(
                                CategoriesEvent.OnCategoryDialogSubmit(
                                    CategoryDialogSubmit.Delete
                                )
                            )
                        },
                        onDismissRequest = {
                            onEvent(
                                CategoriesEvent.OnCategoryDialogToggle(
                                    CategoryDialogToggle.Hidden
                                )
                            )
                        })
                }

                else -> {}
            }
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
        categories = state.categories,
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
        onAddNewCategoryClicked = {
            onEvent(
                CategoriesEvent.OnCategoryDialogToggle(
                    CategoryDialogToggle.Add
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
    categories: List<Category>,
    onEditCategoryClicked: (Category) -> Unit,
    onDeleteCategoryClicked: (Category) -> Unit,
    onAddNewCategoryClicked: () -> Unit,
    onTransactionTypeChange: (TransactionType) -> Unit,
) {

    if (categories.isEmpty()) {
        Text(
            text = "No categories available",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth()
        ) {
            item {
                TransactionTypeSelector(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(56.dp),
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
                    items(items = categories, key = {
                        it.categoryId
                    }) { category ->
                        CategoryItem(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 1.dp,
//                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(4.dp)
                                ), categoryName = category.name, onEditClick = {
                                onEditCategoryClicked(category)
                            }, onDeleteClicked = {
                                onDeleteCategoryClicked(category)
                            })
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
                                ), text = "Add New Category", onClick = {
                                onAddNewCategoryClicked()
                            })
                    }
                }
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    CategoriesScreen(
        state = CategoriesScreenState(
            categories = listOf(
                Category(
                    categoryId = 1L,
                    name = "Fruits",
                    description = "All types of fruits",
                    isExpenseCategory = true
                ),
                Category(
                    categoryId = 2L,
                    name = "Vegetables",
                    description = "All types of vegetables",
                    isExpenseCategory = true
                ),
                Category(
                    categoryId = 3L,
                    name = "Dairy",
                    description = "All types of dairy products",
                    isExpenseCategory = true
                ),
            ),
        ),
        onEvent = {},
    )
}