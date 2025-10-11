package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
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
import com.example.myapplication.logic.TranslatedPattern
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishPatternScreen(
    navController: NavController,
    viewModel: EnglishPatternViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターン") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 成功状態のときのみ「編集」ボタンを表示
                    val successState = uiState as? EnglishPatternUiState.Success
                    if (successState != null) {
                        IconButton(onClick = {
                            // ViewModelが保持している元のCSVデータを取得
                            val csvContent = successState.originalCsv
                            if (csvContent.isNotBlank()) {
                                val encodedCsv = Uri.encode(csvContent)
                                // 「編み図を修正」画面へ遷移
                                navController.navigate(Screen.EditOcrResult.createRoute(true, encodedCsv))
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "修正する")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is EnglishPatternUiState.Success) {
                BottomAppBar(containerColor = Color.White) {
                    TextButton(onClick = { /* TODO: クリップボードにコピーする処理を実装 */ }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "コピー")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("パターンをコピー")
                    }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is EnglishPatternUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EnglishPatternUiState.Success -> {
                PatternContent(
                    state = state,
                    modifier = Modifier.padding(padding)
                )
            }
            is EnglishPatternUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "エラーが発生しました。\n${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PatternContent(
    state: EnglishPatternUiState.Success,
    modifier: Modifier = Modifier
) {
    val translatedPattern = state.translatedPattern ?: return // 念のためnullチェック
    val currentStep = state.highlightedRow

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Abbreviations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(translatedPattern.abbreviations.toList()) { (abbr, desc) ->
            Row {
                Text("${abbr.uppercase()}: ", fontWeight = FontWeight.Bold)
                Text(desc)
            }
        }

        item {
            Text("Instructions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        itemsIndexed(translatedPattern.instructions) { index, instruction ->
            val isHighlighted = index == currentStep
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isHighlighted) PrimaryTeal.copy(alpha = 0.1f) else Color.Transparent,
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isHighlighted) PrimaryTeal else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                Text("Row ${index + 1}: ", fontWeight = FontWeight.Bold)
                Text(instruction)
            }
        }
    }
}