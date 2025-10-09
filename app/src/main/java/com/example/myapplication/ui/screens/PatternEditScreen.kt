package com.example.myapplication.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    // 保存成功時に前の画面に戻る
    LaunchedEffect(uiState) {
        if (uiState is PatternEditUiState.SaveSuccess) {
            Toast.makeText(context, "保存しました！", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        if (uiState is PatternEditUiState.Error) {
            Toast.makeText(context, (uiState as PatternEditUiState.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            // ★★★ 修正: `when`を使って、状態がSuccessの場合のみボタンを有効化 ★★★
            val canInteract = uiState is PatternEditUiState.Success
            val successState = uiState as? PatternEditUiState.Success

            TopAppBar(
                title = { Text("編み図を修正") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { patternEditViewModel.undo() },
                        enabled = canInteract && (successState?.canUndo == true)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "元に戻す")
                    }
                    Button(
                        onClick = { patternEditViewModel.savePattern() },
                        enabled = canInteract
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        // ★★★ 修正: `when`を使って、ViewModelの状態に応じてUIを切り替える ★★★
        when (val state = uiState) {
            is PatternEditUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PatternEditUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is PatternEditUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    // 上半分：編み図グリッド
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(state.patternGrid.firstOrNull()?.size ?: 10),
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        itemsIndexed(state.patternGrid.flatten()) { index, symbol ->
                            val row = index / (state.patternGrid.firstOrNull()?.size ?: 1)
                            val col = index % (state.patternGrid.firstOrNull()?.size ?: 1)
                            val isSelected = state.selectedCell == Pair(row, col)

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
                            selectedSymbol = state.selectedSymbol,
                            onSymbolSelected = { patternEditViewModel.onSymbolSelected(it) }
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { patternEditViewModel.addRow() },
                                modifier = Modifier.weight(1f)
                            ) { Text("行を追加") }
                            Button(
                                onClick = { patternEditViewModel.removeRow() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("行を削除") }
                        }
                    }
                }
            }
            // SaveSuccess時はLaunchedEffectで画面遷移するので、UIは表示しない
            is PatternEditUiState.SaveSuccess -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() // 保存後の処理中を示す
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

