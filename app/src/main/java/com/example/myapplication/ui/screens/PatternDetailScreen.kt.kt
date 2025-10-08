package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.counter.CompactCounterRow
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDetailScreen(
    navController: NavController,
    viewModel: PatternDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val work = uiState.work

    var selectedTabIndex by remember { mutableStateOf(0) }
    var sensorConnected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = work?.title ?: "読み込み中...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EnglishPattern.route) }) {
                        Icon(Icons.Default.Translate, contentDescription = "翻訳")
                    }
                    IconButton(onClick = { navController.navigate(Screen.PatternEdit.route) }) {
                        Icon(Icons.Default.Edit, contentDescription = "修正")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (work != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(BgBase)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text("表面") })
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text("裏面") })
                }

                PatternChart(
                    pattern = uiState.patternData,
                    highlightedRow = work.raw_index,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactCounterRow(
                        label = "現在の段数",
                        count = work.raw_index,
                        onCountChange = { newCount ->
                            if (newCount > work.raw_index) viewModel.incrementRow() else viewModel.decrementRow()
                        }
                    )
                    Divider(color = BgBase, thickness = 1.dp)
                    CompactCounterRow(
                        label = "現在の目数",
                        count = work.stitch_index,
                        onCountChange = { newCount ->
                            if (newCount > work.stitch_index) viewModel.incrementStitch() else viewModel.decrementStitch()
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
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.error ?: "エラーが発生しました。")
            }
        }
    }
}

@Composable
fun PatternChart(
    pattern: List<List<String>>,
    highlightedRow: Int,
    modifier: Modifier = Modifier
) {
    val symbolMap = mapOf("k" to "|", "p" to "•")
    val highlightColor = PrimaryTeal.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pattern.isEmpty()) {
            Text("編み図データがありません", color = Color.Gray)
        } else {
            val columnCount = pattern.firstOrNull()?.size ?: 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount)
            ) {
                itemsIndexed(pattern.flatten()) { index, symbol ->
                    val rowIndex = index / columnCount
                    val backgroundColor = if (rowIndex == highlightedRow) highlightColor else Color.Transparent
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = symbolMap[symbol] ?: symbol, color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
