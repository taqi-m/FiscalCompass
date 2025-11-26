package com.fiscal.compass.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R

@Composable
fun CardWithIcon(
    modifier :Modifier = Modifier,
    @DrawableRes icon: Int,
    label: String,
    description: String,
    onClick: () -> Unit
){
    val iconSize = 50.dp
    Card (
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            CardText(
                label = label,
                description = description,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f)
            )
            CardIcon(
                icon = icon,
                modifier = Modifier
            )
        }
    }

}

@Composable
private fun CardText(modifier: Modifier = Modifier, label: String, description: String) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CardIcon(modifier: Modifier = Modifier, @DrawableRes icon: Int) {
    Icon(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = CircleShape)
            .size(50.dp)
            .padding(10.dp),
        painter = painterResource(icon),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
    )
}

@Preview(showBackground = true)
@Composable
fun CardWithIconPreview() {
    Column {
        CardWithIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            icon = R.drawable.ic_calendar_month_56,
            label = "Monthly Report",
            description = "Get an overview of monthly transactions",
            onClick = {}
        )
        CardWithIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            icon = R.drawable.ic_category_02,
            label = "Category Analysis",
            description = "View report separated by categories",
            onClick = {},
        )
    }
}