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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.Work
import com.example.myapplication.ui.components.counter.CompactCounterRow
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDetailScreen(
    navController: NavController,
    onMenuClick: () -> Unit,, // このパラメータは残しますが、現在は使いません
    viewModel: PatternDetailViewModel
    viewModel: PatternViewModel
) {
    val work by viewModel.work.collectAsState()

    var rowCount by remember { mutableStateOf(3) }
    var stitchCount by remember { mutableStateOf(5) }
    val work by viewModel.work.collectAsState()

    var rowCount by remember { mutableStateOf(3) }
    var stitchCount by remember { mutableStateOf(5) }
    var selectedTab by remember { mutableStateOf(0) }
    var sensorConnected by remember { mutableStateOf(false) }

    LaunchedEffect(work) {
        if (work != null) {
            rowCount = work?.raw_index ?: 0
            stitchCount = work?.stitch_index ?: 0
        }
    }

    LaunchedEffect(work) {
        if (work != null) {
            rowCount = work?.raw_index ?: 0
            stitchCount = work?.stitch_index ?: 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = work?.title ?: "nullでした") },
                title = { Text(text = work?.title ?: "nullでした") },
                navigationIcon = {
                    // サイドバーメニューではなく「戻る」ボタンに変更
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 翻訳ボタンと修正ボタンを追加
                    IconButton(onClick = { navController.navigate(Routes.ENGLISH_PATTERN) }) {
                        Icon(Icons.Default.Translate, contentDescription = "翻訳")
                    }
                    // 修正箇所：onClickに画面遷移を追加
                    IconButton(onClick = { navController.navigate(Routes.PATTERN_EDIT) }) {
                        Icon(Icons.Default.Edit, contentDescription = "修正")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BgBase)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("表面") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("裏面") })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("ここに編み図チャートが表示されます", color = Color.Gray)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactCounterRow(
                    label = "現在の段数",
                    count = rowCount,
                    onCountChange = { newCount ->
                        // Compose 内の状態更新
                        rowCount = newCount
                        // ViewModel の関数呼び出し
                        viewModel.IncrementStitch(request = IncrementStitchRequest(
                            raw_index = rowCount,
                            stitch_index = stitchCount,
                            is_completed = false
                        ))
                    }
                )
                CompactCounterRow(
                    label = "現在の段数",
                    count = rowCount,
                    onCountChange = { newCount ->
                        // Compose 内の状態更新
                        rowCount = newCount
                        // ViewModel の関数呼び出し
                        viewModel.IncrementStitch(request = IncrementStitchRequest(
                            raw_index = rowCount,
                            stitch_index = stitchCount,
                            is_completed = false
                        ))
                    }
                )
                Divider(color = BgBase, thickness = 1.dp)
                CompactCounterRow(
                    label = "現在の目数",
                    count = stitchCount,
                    onCountChange = { newCount ->
                        // Compose 内の状態更新
                        stitchCount = newCount
                        // ViewModel の関数呼び出し
                        viewModel.IncrementStitch(request = IncrementStitchRequest(
                            raw_index = rowCount,
                            stitch_index = stitchCount,
                            is_completed = false
                        ))
                    }
                )
                CompactCounterRow(
                    label = "現在の目数",
                    count = stitchCount,
                    onCountChange = { newCount ->
                        // Compose 内の状態更新
                        stitchCount = newCount
                        // ViewModel の関数呼び出し
                        viewModel.IncrementStitch(request = IncrementStitchRequest(
                            raw_index = rowCount,
                            stitch_index = stitchCount,
                            is_completed = false
                        ))
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) { Text("段数モード") }
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) { Text("目数モード", color = Color.DarkGray) }
                }
                Divider(color = BgBase, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("センサー接続", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Switch(
                        checked = sensorConnected,
                        onCheckedChange = { sensorConnected = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal)
                    )
                }
            }
        }
    }
}
