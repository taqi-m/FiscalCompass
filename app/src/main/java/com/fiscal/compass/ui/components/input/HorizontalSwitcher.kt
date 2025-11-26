package com.fiscal.compass.ui.components.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.fiscal.compass.R
import com.fiscal.compass.ui.theme.FiscalCompassTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonthSelector(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    dateFormatter: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()),
    textStyle: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black)
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_left_24),
                contentDescription = "Previous month"
            )
        }

        Text(
            text = dateFormatter.format(currentDate),
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Center,
            style = textStyle
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_keyboard_arrow_right_24),
                contentDescription = "Next month"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthSelectorPreview() {
    FiscalCompassTheme {
        MonthSelector(
            currentDate = Date(),
            onPreviousMonth = {},
            onNextMonth = {}
        )
    }
}

// Usage (in a screen):
// var currentDate by remember { mutableStateOf(LocalDate.now()) }
// MonthSelector(
//     currentDate = currentDate,
//     onPreviousMonth = { currentDate = currentDate.minusMonths(1) },
//     onNextMonth = { currentDate = currentDate.plusMonths(1) },
// )
