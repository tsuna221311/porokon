package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.Work
import com.example.myapplication.ui.components.common.PatternListItem
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.SecondarySalmon

@Composable
fun DashboardScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    dashboardViewModel: DashboardViewModel
) {
    // ViewModelの状態に応じて、読み込み中／成功／エラー画面を切り替える
    val uiState by dashboardViewModel.dashboardUiState.collectAsState()

    when (uiState) {
        is DashboardUiState.Loading -> LoadingScreen()
        is DashboardUiState.Success -> ResultScreen(
            navController = navController,
            onMenuClick = onMenuClick,
            works = (uiState as DashboardUiState.Success).works
        )
        is DashboardUiState.Error -> ErrorScreen()
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "エラーが発生しました", modifier = Modifier.padding(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    works: List<Work>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${works.size}件の作品") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // ★★★ 新しい実装 ★★★
                    // 「＋」ボタンが押されたら、モード選択画面に遷移する
                    navController.navigate(Screen.SelectMode.route)
                },
                containerColor = PrimaryTeal
            ) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = "新規作成",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        if (works.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "作品がありません",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(works) { work ->
                    PatternListItem(
                        title = work.title,
                        description = work.description,
                        icon = if (work.is_completed) Icons.Default.Check else Icons.Default.Edit,
                        iconColor = if (work.is_completed) SecondarySalmon else PrimaryTeal,
                        onClick = {
                            // ★★★ 新しい実装 ★★★
                            // 作品がタップされたら、正しいルートで詳細画面に遷移する
                            navController.navigate(Screen.PatternView.createRoute(work.id))
                        }
                    )
                }
            }
        }
    }
}

