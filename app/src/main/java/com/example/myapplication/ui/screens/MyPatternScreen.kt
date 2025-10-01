package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.theme.PrimaryTeal

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
        },
        // ★★★ フローティングアクションボタンを追加 ★★★
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.OCR_CAPTURE) },
                containerColor = PrimaryTeal
            ) {
                Icon(Icons.Default.Add, contentDescription = "英文パターンから新しい編み図を追加")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("保存した編み図の一覧がここに表示されます。")
        }
    }
}


