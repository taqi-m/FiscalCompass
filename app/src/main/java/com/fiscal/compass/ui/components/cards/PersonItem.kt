package com.fiscal.compass.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonItem(
    modifier: Modifier = Modifier,
    personName: String,
    personType: String,
    onEditClick: () -> Unit,
    onDeleteClicked: () -> Unit
) {

    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(true, onClick = {})
            .padding(start = 16.dp)
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PersonTextBlock(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            personName = personName,
            personType = personType
        )



        Row(

        ) {
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
                    contentDescription = "Edit"
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

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PersonItemPreview() {
    PersonItem(
        personName = "John Doe",
        personType = "Admin",
        onEditClick = {},
        onDeleteClicked = {}
    )
}



@Composable
fun PersonTextBlock(
    modifier: Modifier = Modifier,
    personName: String,
    personType: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "$personName ",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}