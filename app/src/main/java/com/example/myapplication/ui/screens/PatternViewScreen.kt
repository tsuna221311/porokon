package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternViewScreen( // 関数名をファイル名に合わせて変更
    navController: NavController,
    viewModel: PatternDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val work = uiState.work

    var selectedTabIndex by remember { mutableStateOf(0) }
    val currentPattern = uiState.patternData

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
                    IconButton(onClick = {
                        navController.navigate(Screen.EnglishPattern.route)
                    }) {
                        Icon(Icons.Default.Translate, contentDescription = "翻訳")
                    }
                    IconButton(onClick = { navController.navigate(Screen.PatternEdit.createRoute(work?.id ?: 0)) }) {
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
                    pattern = currentPattern,
                    highlightedRow = work.raw_index,
                    modifier = Modifier.weight(1f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactCounterRow(
                            label = "現在の段数",
                            count = work.raw_index + 1,
                            onDecrement = { viewModel.decrementRow() },
                            onIncrement = { viewModel.incrementRow() }
                        )

                        HorizontalDivider(color = BgBase, thickness = 1.dp)

                        CompactCounterRow(
                            label = "現在の目数",
                            count = work.stitch_index + 1,
                            onDecrement = { viewModel.decrementStitch() },
                            onIncrement = { viewModel.incrementStitch() }
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
private fun PatternChart(
    pattern: List<List<String>>,
    highlightedRow: Int,
    modifier: Modifier = Modifier
) {
    val symbolMap = mapOf(
        "k" to "｜", "p" to "—", "k2tog" to "╱", "ssk" to "╲", "yo" to "○", "k3tog" to "＞"
    )
    val highlightColor = PrimaryTeal.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pattern.all { it.isEmpty() }) {
            Text("編み図データがありません", color = Color.Gray)
        } else {
            val columnCount = pattern.maxOfOrNull { it.size }?.takeIf { it > 0 } ?: 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                userScrollEnabled = true
            ) {
                itemsIndexed(pattern.flatten()) { index, symbol ->
                    val rowIndex = index / columnCount
                    val backgroundColor = if (rowIndex == highlightedRow) highlightColor else Color.Transparent
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(backgroundColor)
                            .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (symbol != "^" && symbol != "-") {
                            Text(
                                text = symbolMap[symbol] ?: symbol,
                                color = Color.DarkGray, fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCounterRow(
    label: String,
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CounterButton(text = "-") { onDecrement() }
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal
            )
            CounterButton(text = "+") { onIncrement() }
        }
    }
}

@Composable
private fun CounterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


