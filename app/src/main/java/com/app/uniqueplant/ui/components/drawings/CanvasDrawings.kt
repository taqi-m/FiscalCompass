package com.app.uniqueplant.ui.components.drawings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RelativeCircle(
    modifier: Modifier,
    promptTextPosition: Offset,
    promptTextHeight: Float,
) {
    val canvasColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val density = LocalDensity.current

    // Calculate circle position - 8dp below the text's bottom edge
    val circleBottomOffset = with(density) { 16.dp.toPx() }
    Canvas(
        modifier = modifier
    ) {
        // Only draw circle if we have valid position for the prompt text
        if (promptTextPosition != Offset.Zero) {
            // Calculate circle center position: x centered on screen,
            // y positioned relative to the prompt text's bottom plus the offset
            val circleY = promptTextPosition.y + promptTextHeight + circleBottomOffset
            val circleRadius = size.width * 0.65f

            drawCircle(
                color = canvasColor,
                radius = circleRadius,
                center = Offset(size.width * 0.5f, circleY - circleRadius)
            )
        }
    }
}

@Composable
fun BottomSpikes(
    modifier: Modifier
) {
    val canvasColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

    Canvas(
        modifier = modifier
    ) {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, size.height * 0.7f)
            lineTo(size.width * 0.2f, size.height * 0.4f)
            lineTo(size.width * 0.4f, size.height * 0.6f)
            lineTo(size.width * 0.6f, size.height * 0.3f)
            lineTo(size.width * 0.8f, size.height * 0.5f)
            lineTo(size.width, size.height * 0.25f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path = path, color = canvasColor)
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSpikesPreview() {
    BottomSpikes(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}


@Composable
fun MountainSpikes(
    modifier: Modifier
) {
    val canvasColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val overlayColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

//     Mountain spikes at the bottom
    Canvas(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main mountain structure
        val leftMountain = Path().apply {
            moveTo(0f, size.height) // Bottom-left
            lineTo(0f, size.height ) // Up along left edge
            lineTo(size.width * 0.3f, size.height * 0.175f) // Peak 1
            lineTo(size.width * 0.6f, size.height) // Valley
            close()
        }
        drawPath(path = leftMountain, color = canvasColor)

        val rightMountain = Path().apply {
            moveTo(size.width * 0.4f, size.height) // Bottom-left
            lineTo(size.width * 0.7f, size.height * 0.175f) // Peak 1
            lineTo(size.width , size.height) // Valley
            close()
        }
        drawPath(path = rightMountain, color = canvasColor)

        val middleMountain = Path().apply {
            moveTo(size.width * 0.2f, size.height) // Bottom-left
            lineTo(size.width * 0.5f, size.height * 0.05f) // Peak 1
            lineTo(size.width * 0.8f, size.height) // Valley
            close()
        }
        drawPath(path = middleMountain, color = canvasColor)
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun MountainSpikesPreview() {
    MountainSpikes(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}