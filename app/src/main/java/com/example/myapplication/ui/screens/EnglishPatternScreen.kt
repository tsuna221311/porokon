package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
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
fun EnglishPatternScreen(
    navController: NavController,
    viewModel: EnglishPatternViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val translatedPattern = uiState.translatedPattern
    val currentStep = 3 // ダミーのハイライト位置 (4段目)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターン (ダミー)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        bottomBar = {
            if (translatedPattern != null) {
                BottomAppBar(containerColor = Color.White) {
                    TextButton(onClick = { /* TODO: Copy to clipboard */ }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "コピー")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("パターンをコピー")
                    }
                }
            }
        }
    ) { padding ->
        // ViewModelの状態に応じてUIを切り替える
        if (uiState.isLoading || translatedPattern == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- Abbreviations (略語) ---
                item {
                    Text("Abbreviations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        translatedPattern.abbreviations.forEach { (abbr, desc) ->
                            Row {
                                Text("${abbr.uppercase()}: ", fontWeight = FontWeight.Bold)
                                Text(desc)
                            }
                        }
                    }
                }
                // --- Instructions (手順) ---
                item {
                    Text("Instructions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        translatedPattern.instructions.forEachIndexed { index, instruction ->
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
            }
        }
    }
}
