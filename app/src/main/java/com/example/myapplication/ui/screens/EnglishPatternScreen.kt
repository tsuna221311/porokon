package com.example.myapplication.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
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
import com.example.myapplication.logic.TranslatedPattern
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishPatternScreen(
    navController: NavController,
    viewModel: EnglishPatternViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターン") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Chart")
                    }
                }
            )
        },
        bottomBar = {
            val pattern = uiState.translatedPattern
            if (pattern != null) {
                BottomAppBar(containerColor = Color.White) {
                    TextButton(
                        onClick = {
                            val textToCopy = pattern.instructions.joinToString("\n")
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Pattern", textToCopy))
                            viewModel.showCopiedMessage()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("パターンをコピー")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    val errorMsg = uiState.error
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMsg ?: "不明なエラー")
                    }
                }

                uiState.translatedPattern != null -> {
                    // ★★ 修正箇所 ★★ nullチェック後に非nullとして PatternContent に渡す
                    uiState.translatedPattern?.let { patternContent ->
                        PatternContent(
                            padding = padding,
                            translatedPattern = patternContent,
                            currentStep = uiState.currentStep
                        )
                    }
                }
            }

            if (uiState.showCopiedMessage) {
                SnackbarHost(
                    hostState = remember { SnackbarHostState() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Snackbar { Text("コピーしました！") }
                }
            }
        }
    }
}

@Composable
private fun PatternContent(
    padding: PaddingValues,
    translatedPattern: TranslatedPattern,
    currentStep: Int
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                "Abbreviations",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                translatedPattern.abbreviations.forEach { (abbr, desc) ->
                    Row {
                        Text("$abbr: ", fontWeight = FontWeight.Bold)
                        Text(desc)
                    }
                }
            }
        }
        item {
            Text(
                "Instructions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
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
