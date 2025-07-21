package com.app.uniqueplant.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.ui.components.DataEntryTextField
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.util.Date

@Composable
fun DeleteDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteConfirmed()
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
fun DeleteDialogPreview() {
    UniquePlantTheme {
        DeleteDialog(
            isVisible = true,
            onDismissRequest = {},
            onDeleteConfirmed = {}
        )
    }
}

@Composable
fun EditDialog(
    isVisible: Boolean,
    transaction: Any,
    onDismissRequest: () -> Unit,
    onEditConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Edit Transaction") },
        text = {
            when (transaction) {
                is Expense -> {
                    EditExpenseContent(
                        expense = transaction
                    )
                }

                is Income -> {

                }

                else -> Text(
                    text = "Invalid transaction selected!!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onEditConfirmed()
                onDismissRequest()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun EditDialogPreview() {
    UniquePlantTheme {
        EditDialog(
            isVisible = true,
            transaction = Expense(
                amount = 50.0,
                description = "Groceries",
                date = Date(),
                categoryId = 1,
                userId = "1"
            ),
            onDismissRequest = {},
            onEditConfirmed = {}
        )
    }
}


@Composable
fun EditExpenseContent(
    expense: Expense,
){
    Column {
        DataEntryTextField(
            label = "Amount",
            value = expense.amount.toString(),
            onValueChange = {},
            isError = false,
            errorMessage = null
        )
        DataEntryTextField(
            label = "Description",
            value = expense.description,
            onValueChange = {},
            isError = false,
            errorMessage = null
        )
    }
}

@Preview
@Composable
fun EditExpenseContentPreview() {
    UniquePlantTheme {
        EditExpenseContent(
            expense = Expense(
                description = "Groceries",
                amount = 50.0,
                date = Date(),
                categoryId = 1,
                userId = "1"
            )
        )
    }
}