package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.model.Work
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.PrimaryTeal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    dashboardViewModel: DashboardViewModel
) {
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
                title = { Text("進行中の作品 (${works.size}件)") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.SelectMode.route) },
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
                    text = "進行中の作品はありません",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(works) { work ->
                    PatternListItem(
                        title = work.title,
                        description = work.description ?: "",
                        createdAt = work.created_at,
                        updatedAt = work.updated_at,
                        onClick = {
                            navController.navigate(Screen.PatternView.createRoute(work.id))
                        }
                    )
                }
            }
        }
    }
}

/**
 * 日付の文字列 (ISO 8601形式) を "yyyy/MM/dd HH:mm" 形式にフォーマットするヘルパー関数
 */
private fun formatDateString(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString // パースに失敗した場合は元の文字列を返す
    }
}

/**
 * 作品リストの各項目を表示するUIコンポーネント
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatternListItem(
    title: String,
    description: String,
    createdAt: String,
    updatedAt: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (description.isNotBlank()) {
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            // 登録日と更新日を表示する部分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "更新: ${formatDateString(updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "作成: ${formatDateString(createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

