package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R


@Composable
fun TransactionCardOptions(
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
){
    var menuExpanded by remember { mutableStateOf(false) }
    Column {
        IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more_vert_24), // Replace with your income icon
                contentDescription = "Income Icon",
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onEditClicked()
                    menuExpanded = false
                },
                text = {
                    Text(text = "Edit")
                }
            )

            DropdownMenuItem(
                onClick = {
                    onDeleteClicked()
                    menuExpanded = false
                },
                text = {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}