package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen // 修正: Screen をインポート

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPatternsScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("マイ編み図") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("ここに「マイ編み図」の一覧が表示されます。")
            // 例: Dashboardへの遷移ボタン（必要に応じて）
            /*
            Button(onClick = { navController.navigate(Screen.Dashboard.route) }) {
                Text("ダッシュボードへ")
            }
            */
        }
    }
}