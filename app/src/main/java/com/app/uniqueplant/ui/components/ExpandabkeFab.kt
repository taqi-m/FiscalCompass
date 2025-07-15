package com.app.uniqueplant.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class FabOption(
    val icon: Any, // Can be ImageVector or Painter
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun ExpandableFab(
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    fabOptions: List<FabOption>
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.End,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
    ) {
        fabOptions.forEach { option ->
            androidx.compose.animation.AnimatedVisibility(visible = isExpanded) {
                Card(
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    onClick = {
                        option.onClick()
                        onExpandToggle()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        when (val icon = option.icon) {
                            is ImageVector -> Icon(icon, contentDescription = option.label)
                            is Painter -> Icon(icon, contentDescription = option.label)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option.label)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        FloatingActionButton(
            onClick = { onExpandToggle() },
        ) {
            val rotation by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (isExpanded) 45f else 0f
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
)
@Composable
fun ExpandableFabPreview() {
    var isExpanded by remember { mutableStateOf(true) }
    val fabOptions = listOf(
        FabOption(
            icon = Icons.Filled.Add,
            label = "Add Item",
            onClick = { /* TODO: Handle Add Item click */ }
        ),
        FabOption(
            icon = Icons.Filled.Close,
            label = "Remove Item",
            onClick = { /* TODO: Handle Remove Item click */ }
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = androidx.compose.ui.Alignment.BottomEnd
    ) {
        ExpandableFab(
            isExpanded = isExpanded,
            onExpandToggle = { isExpanded = !isExpanded },
            fabOptions = fabOptions
        )
    }
}