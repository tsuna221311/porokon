package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Routes

@Composable
fun ConfirmPhotoScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("（撮影した写真のプレビュー）", color = Color.White)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("撮り直す", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = { navController.navigate(Routes.SAVE_PATTERN) }) {
                    Text("OK", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

