package com.app.uniqueplant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme

data class FabOption(
    val icon: Any,
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
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        fabOptions.forEach { option ->
            AnimatedVisibility(visible = isExpanded) {
                Card(
                    modifier = Modifier.padding(vertical = 4.dp),
                    onClick = {
                        option.onClick()
                        onExpandToggle()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (val icon = option.icon) {
                            is ImageVector -> Icon(icon, contentDescription = option.label)
                            is Painter -> Icon(icon, contentDescription = option.label)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option.label
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        FloatingActionButton(
            shape = CircleShape,
            onClick = { onExpandToggle() },
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 135f else 0f
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
            onClick = { }
        ),
        FabOption(
            icon = Icons.Filled.Close,
            label = "Remove Item",
            onClick = { }
        )
    )
    UniquePlantTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            ExpandableFab(
                isExpanded = isExpanded,
                onExpandToggle = { isExpanded = !isExpanded },
                fabOptions = fabOptions
            )
        }
    }
}