package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.components.counter.CompactCounterRow
// ↓ あなたのプロジェクトの正しいパスに修正してください
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.theme.BgBase
// import com.example.myapplication.viewmodels.PatternViewModel // ViewModelのパスをインポートしてください

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternViewScreen(
    navController: NavController,
    viewModel: PatternViewModel
) {
    // UIの状態をViewModelから監視
    val uiState by viewModel.uiState.collectAsState()
    // 選択中のタブの状態（0: 表面, 1: 裏面）
    var selectedTab by remember { mutableStateOf(0) }
    // センサー接続状態（このコードでは未使用ですが残しておきます）
    var sensorConnected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.work?.title ?: "読み込み中...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 翻訳画面へ
                    IconButton(onClick = { navController.navigate(Routes.ENGLISH_PATTERN) }) {
                        Icon(Icons.Default.Translate, contentDescription = "翻訳")
                    }
                    // 編集画面へ
                    IconButton(onClick = { navController.navigate(Routes.PATTERN_EDIT) }) {
                        Icon(Icons.Default.Edit, contentDescription = "修正")
                    }
                }
            )
        }
    ) { paddingValues ->
        // データ読み込み中の表示
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.work != null) {
            // データ読み込み完了後の表示
            val work = uiState.work!!
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(BgBase)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 表面・裏面を切り替えるタブ
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("表面") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("裏面") })
                }

                // 編み図チャート表示エリア
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ここに編み図チャートが表示されます", color = Color.Gray)
                }

                //カウンター表示エリア
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 現在の段数カウンター
                    CompactCounterRow("現在の段数", work.rowIndex) { newCount -> // 修正: .row_index -> .rowIndex
                        if (newCount > work.rowIndex) viewModel.incrementRow() else viewModel.decrementRow() // 修正
                    }

                    HorizontalDivider(color = BgBase, thickness = 1.dp)

                    // 現在の目数カウンター
                    CompactCounterRow("現在の目数", work.stitchIndex) { /* TODO */ } // 修正: .stitch_index -> .stitchIndex
                }
            }
        }
    }
}