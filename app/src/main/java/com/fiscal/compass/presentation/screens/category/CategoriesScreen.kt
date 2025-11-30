package com.fiscal.compass.presentation.screens.category

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiscal.compass.presentation.mappers.toCategory
import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.ui.components.buttons.AddNewButton
import com.fiscal.compass.ui.components.cards.CategoryItem
import com.fiscal.compass.ui.components.dialogs.AddCategoryDialog
import com.fiscal.compass.ui.components.dialogs.DeleteCategoryDialog
import com.fiscal.compass.ui.components.dialogs.EditCategoryDialog
import com.fiscal.compass.ui.components.input.TypeSwitch
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    appNavController: NavHostController,
    state: CategoriesScreenState,
    onEvent: (CategoriesEvent) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val uiState = state.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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



@Composable
fun CategoriesScreenContent(
    state: CategoriesScreenState, onEvent: (CategoriesEvent) -> Unit, modifier: Modifier = Modifier
) {
    CategoriesList(
        modifier = modifier.fillMaxSize(),
        uiState = state.uiState,
        transactionType = state.transactionType,
        categoriesList = state.categories,
        onEditCategoryClicked = if (state.canEdit) {
            { category ->
                onEvent(CategoriesEvent.OnCategoryDialogToggle(CategoryDialogToggle.Edit(category)))
            }
        } else null,
        onDeleteCategoryClicked = if (state.canDelete) {
            { category ->
                onEvent(
                    CategoriesEvent.OnCategoryDialogToggle(
                        CategoryDialogToggle.Delete(category)
                    )
                )
            }
        } else null,
        onAddNewCategoryClicked = if (state.canAdd) {
            { categoryGroup ->
                onEvent(
                    CategoriesEvent.OnCategoryDialogToggle(
                        CategoryDialogToggle.Add(
                            categoryGroup
                        )
                    )
                )
            }
        } else null,
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
    categoriesList: List<CategoryUi>,
    onEditCategoryClicked: ((CategoryUi) -> Unit)? = null,
    onDeleteCategoryClicked: ((CategoryUi) -> Unit)? = null,
    onAddNewCategoryClicked: ((Long?) -> Unit)? = null,
    onTransactionTypeChange: (TransactionType) -> Unit,
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth()
    )
    {
        stickyHeader {
            val selectedIndex = TransactionType.entries.indexOf(transactionType)
            TypeSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = 8.dp, top = 4.dp)
                    .height(40.dp),
                shape = MaterialTheme.shapes.small,
                typeOptions = TransactionType.entries.map { it.name },
                selectedTypeIndex = selectedIndex,
                onTypeSelected = { index ->
                    onTransactionTypeChange(TransactionType.entries[index])
                }
            )
        }
        when (uiState) {
            is UiState.Loading -> LoadingCategories()


            else -> {
                items(categoriesList, key = { it.categoryId}) {category ->
                    CategoryItem(
                        modifier = Modifier.padding(vertical = 4.dp),
                        categoryName = category.name,
                        onEditClick = {
                            onEditCategoryClicked?.invoke(category)
                        },
                        onDeleteClicked = {
                            onDeleteCategoryClicked?.invoke(category)
                        }
                    )
                }

                /*categoriesList.forEach { (category, categories) ->
                    item {
                        ExpandableChipCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            headerItem = category,
                            showIcon = true,
                            trailingIcon = onAddNewCategoryClicked?.let {
                                    {
                                        FilledTonalIconButton(
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
                                    }
                                },
                            chips = categories,
                            onCardClick = {
                                onEditCategoryClicked?.invoke(category)
                            },
                            onCardLongClick = {
                                onDeleteCategoryClicked?.invoke(category)
                            },
                            onChipClick = { category -> onEditCategoryClicked?.invoke(category) },
                            onChipLongClick = { category -> onDeleteCategoryClicked?.invoke(category) },
                            initiallyExpanded = true,
                            chipToLabel = { it.name }
                        )
                    }
                }*/

                onAddNewCategoryClicked?.let {
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
                                ), text = "Add Category",
                            onClick = {
                                onAddNewCategoryClicked(null)
                            })
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(
                            WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
                        )
                    )
                }
            }
        }
    }
}

private fun LazyListScope.LoadingCategories() {
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
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = false, showSystemUi = false
)
@Composable
fun CategoriesScreenPreview() {
    val appNavController = rememberNavController()
    val state = CategoriesScreenState(
        uiState = UiState.Idle,
        canAdd = true,
        canEdit = true,
        canDelete = true,
        categories = CategoryUi.dummyList,
        transactionType = TransactionType.EXPENSE,
        currentDialog = CategoriesDialog.Hidden,
        dialogState = CategoryDialogState.Idle
    )
    FiscalCompassTheme { CategoriesScreen(appNavController = appNavController, state = state, onEvent = {}) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun CategoriesScreenNoAddPreview() {
    val appNavController = rememberNavController()
    val state = CategoriesScreenState(
        uiState = UiState.Idle,
        canAdd = false,
        canEdit = true,
        canDelete = true,
        categories = CategoryUi.dummyList,
        transactionType = TransactionType.EXPENSE,
        currentDialog = CategoriesDialog.Hidden,
        dialogState = CategoryDialogState.Idle
    )
    FiscalCompassTheme { CategoriesScreen(appNavController = appNavController, state = state, onEvent = {}) }
}