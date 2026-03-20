package com.fiscal.compass.ui.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    categoryName: String,
    onEditClick: () -> Unit,
    onDeleteClicked: () -> Unit,
    description: String? = null
) {

    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(true, onClick = {})
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 2.dp),
                text = categoryName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            description?.takeIf { it.isNotBlank() }?.let { desc ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(25)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Row {
            /*IconButton(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_circle_24),
                    contentDescription = "Edit"
                )
            }*/
            IconButton(
                onClick = { menuExpanded = !menuExpanded }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_horiz_24),
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {

                DropdownMenuItem(
                    onClick = {
                        onEditClick()
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
}

@Preview
@Composable
fun CategoryItemPreview() {
    FiscalCompassTheme {
        CategoryItem(
            categoryName = "Groceries",
            description = "Food and groceries",
            onEditClick = {},
            onDeleteClicked = {},
        )
    }
}