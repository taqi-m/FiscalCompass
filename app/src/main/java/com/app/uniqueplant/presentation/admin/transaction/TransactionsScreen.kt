package com.app.uniqueplant.presentation.admin.transaction

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@Composable
fun TransactionsScreen() {
    // This is where the dashboard UI will be implemented
    // For now, we can just display a simple text or placeholder
    // You can use MaterialTheme, Scaffold, etc. to build the UI
    // Example:
     Text(text = "Transactions Screen")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionsScreenPreview() {
     UniquePlantTheme {
         TransactionsScreen()
     }
}