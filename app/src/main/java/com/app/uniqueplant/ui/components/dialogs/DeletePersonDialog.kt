package com.app.uniqueplant.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DeletePersonDialog(
    personName: String?,
    onDismissRequest: () -> Unit,
    onDeleteConfirm: () -> Unit
) {

    if (personName.isNullOrEmpty()) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Delete Person") },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete the Person '$personName'? \n",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500

                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDeleteConfirm, colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.error,
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}