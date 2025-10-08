package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.example.myapplication.ui.components.PatternChart
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

// プレビュー用のダミー編み図データ
val dummyPatternFromImage = listOf(
    // 12段目 (一番上)
    listOf("p", "p", "-", "-", "k", "k", "k", "-"),
    listOf("p", "p", "-", "-", "k2tog", "^", "k", "-"),
    listOf("p", "p", "-", "-", "k", "k", "k", "k"),
    listOf("p", "p", "-", "ssk", "^", "k", "k", "k"),
    listOf("p", "p", "-", "k", "k", "k", "k", "k"),
    listOf("p", "p", "-", "k", "k2tog", "^", "k", "k"),
    listOf("p", "p", "ssk", "^", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k2tog", "^", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k")
).reversed() // 編み図は下から上なのでリストを逆順にする

val dummyPatternReverse = dummyPatternFromImage.map { row ->
    row.map { symbol ->
        when (symbol) {
            "k" -> "p"
            "p" -> "k"
            else -> symbol
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternViewScreen(
    navController: NavController
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var highlightedRow by remember { mutableStateOf(3) } // 4段目を初期ハイライト
    var currentStitch by remember { mutableStateOf(5) }

    val currentPattern = if (selectedTabIndex == 0) {
        dummyPatternFromImage
    } else {
        dummyPatternReverse
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("編み図プレビュー (ダミー)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = {}, enabled = false) { Icon(Icons.Default.Translate, contentDescription = "翻訳") }
                    IconButton(onClick = {}, enabled = false) { Icon(Icons.Default.Edit, contentDescription = "修正") }
                }
            )
        },
        containerColor = BgBase
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text("表面") })
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text("裏面") })
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PatternChart(
                    pattern = currentPattern,
                    highlightedRow = highlightedRow,
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
                        // --- 現在の段数カウンター ---
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("現在の段数", fontWeight = FontWeight.SemiBold)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CounterButton(text = "-") {
                                    if (highlightedRow > 0) {
                                        highlightedRow--
                                    }
                                }
                                Text(
                                    text = (highlightedRow + 1).toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal
                                )
                                CounterButton(text = "+") {
                                    if (highlightedRow < currentPattern.size - 1) {
                                        highlightedRow++
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = BgBase, thickness = 1.dp)

                        // --- 現在の目数カウンター ---
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("現在の目数", fontWeight = FontWeight.SemiBold)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CounterButton(text = "-") { if (currentStitch > 0) currentStitch-- }
                                Text(
                                    text = (currentStitch + 1).toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal
                                )
                                CounterButton(text = "+") { currentStitch++ }
                            }
                        }
                    }
                }
            }
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
