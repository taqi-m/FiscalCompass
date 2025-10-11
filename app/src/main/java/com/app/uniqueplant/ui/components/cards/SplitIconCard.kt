package com.app.uniqueplant.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun SplitIconCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row (
        modifier = modifier.height(75.dp)
    ) {
        TextCard(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            title = title,
            description = description
        )
        VerticalDivider(modifier = Modifier.fillMaxHeight().padding(horizontal = 4.dp), thickness = 2.dp)
        IconCard(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            icon = icon,
            onClick = onClick
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun SplitIconCardPreview() {
    UniquePlantTheme {
        SplitIconCard(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            title = "Expenses",
            description = "24 expenses to sync",
            icon = {
                Icon(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    painter = painterResource(R.drawable.ic_cloud_done_96),
                    contentDescription = null
                )
            },
            onClick = {}
        )
    }
}

@Composable
private fun TextCard(modifier: Modifier = Modifier, title: String, description: String){
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun IconCard(modifier: Modifier = Modifier, icon: @Composable () -> Unit, onClick: () -> Unit){
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            icon()
        }
    }
}