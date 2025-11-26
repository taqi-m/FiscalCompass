package com.fiscal.compass.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.ui.components.input.DataEntryTextField

@Composable
fun EditCategoryDialog(
    category: Category?,
    onEditCategory: (Category) -> Unit,
    onDismiss: () -> Unit
) {

    if (category == null) {
        onDismiss()
        return
    }

    var categoryName by remember { mutableStateOf(category.name) }
    var categoryDescription by remember { mutableStateOf(category.description ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Edit Category"
            )
        },
        text = {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                DataEntryTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = "Title",
                    placeholder = "Enter new title",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                DataEntryTextField(
                    value = categoryDescription,
                    onValueChange = { categoryDescription = it },
                    label = "Description",
                    placeholder = "Enter new description",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEditCategory(
                        category.copy(
                            name = categoryName,
                            description = categoryDescription
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = "Save"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}

@Preview
@Composable
fun EditCategoryDialogPreview() {
    val category = Category(
        name = "Gardening Tools",
        description = "Tools for gardening",
        isExpenseCategory = true
    )
    EditCategoryDialog(
        category = category,
        onEditCategory = {},
        onDismiss = {})
}