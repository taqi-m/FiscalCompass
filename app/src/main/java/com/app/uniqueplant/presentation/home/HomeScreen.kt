package com.app.uniqueplant.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.uniqueplant.presentation.auth.AuthViewModel
import com.app.uniqueplant.presentation.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Unique Plant",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Auth.route) {
                    popUpTo("home_screen") { inclusive = true }
                }
            }) {
                Text("Logout")
            }
        }
    }
}
