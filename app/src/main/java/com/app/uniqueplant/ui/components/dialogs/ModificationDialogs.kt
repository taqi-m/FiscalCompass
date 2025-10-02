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
import com.app.uniqueplant.data.local.model.ExpenseEntity
import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.ui.components.input.DataEntryTextField
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import java.util.Date

@Composable
fun DeleteTransactionDialog(
    onDismissRequest: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Delete Transaction") },
        text = { Text("Are you sure you want to delete this entry?") },
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

@Preview
@Composable
fun DeleteDialogPreview() {
    UniquePlantTheme {
        DeleteTransactionDialog(
            onDismissRequest = {},
            onDeleteConfirmed = {}
        )
    }
}

@Composable
fun EditTransactionDialog(
    transaction: Any,
    onDismissRequest: () -> Unit,
    onEditConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Edit Transaction") },
        text = {
            when (transaction) {
                is ExpenseEntity -> {
                    EditExpenseContent(
                        expense = transaction
                    )
                }

                is IncomeEntity -> {

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
        EditTransactionDialog(
            transaction = ExpenseEntity(
                amount = 50.0,
                description = "Groceries",
                date = Date().time,
                categoryId = 1,
                userId = "1"
            ),
            onDismissRequest = {}
        ) {}
    }
}


@Composable
fun EditExpenseContent(
    expense: ExpenseEntity,
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
            expense = ExpenseEntity(
                description = "Groceries",
                amount = 50.0,
                date = Date().time,
                categoryId = 1,
                userId = "1"
            )
        )
    }
}