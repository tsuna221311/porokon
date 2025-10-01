package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternEditScreen(
    navController: NavController,
    patternEditViewModel: PatternEditViewModel = viewModel()
) {
    val uiState by patternEditViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("編み図を修正") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 「元に戻す」ボタンを追加
                    IconButton(
                        onClick = { patternEditViewModel.undo() },
                        enabled = uiState.canUndo // canUndoがtrueの時だけ押せる
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "元に戻す")
                    }
                    Button(onClick = {
                        patternEditViewModel.savePattern()
                        navController.popBackStack()
                    }) {
                        Text("保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 上半分：編み図グリッド
            LazyVerticalGrid(
                columns = GridCells.Fixed(uiState.patternGrid.firstOrNull()?.size ?: 10),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                // flatten()で2次元配列を1次元にして表示
                itemsIndexed(uiState.patternGrid.flatten()) { index, symbol ->
                    val row = index / (uiState.patternGrid.firstOrNull()?.size ?: 1)
                    val col = index % (uiState.patternGrid.firstOrNull()?.size ?: 1)
                    val isSelected = uiState.selectedCell == Pair(row, col)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryTeal else Color.LightGray
                            )
                            .clickable { patternEditViewModel.onCellClicked(row, col) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = symbol, fontWeight = if (symbol != "-") FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            // 下半分：編集ツール
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("記号を選択", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                SymbolPalette(
                    selectedSymbol = uiState.selectedSymbol,
                    onSymbolSelected = { patternEditViewModel.onSymbolSelected(it) }
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { patternEditViewModel.addRow() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("行を追加")
                    }
                    Button(
                        onClick = { patternEditViewModel.removeRow() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("行を削除")
                    }
                }
            }
        }
    }
}

@Composable
fun SymbolPalette(selectedSymbol: String, onSymbolSelected: (String) -> Unit) {
    val symbols = listOf("k", "p", "yo", "k2tog", "ssk", "-")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        symbols.forEach { symbol ->
            Button(
                onClick = { onSymbolSelected(symbol) },
                shape = RoundedCornerShape(8.dp),
                colors = if (symbol == selectedSymbol) ButtonDefaults.buttonColors(containerColor = PrimaryTeal) else ButtonDefaults.buttonColors()
            ) {
                Text(symbol, fontWeight = FontWeight.Bold)
            }
        }
    }
}

