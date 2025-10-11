package com.example.myapplication.ui.screens

import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEnglishPatternScreen(
    navController: NavController,
    viewModel: EditEnglishPatternViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 保存成功時の画面遷移
    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is EditEnglishPatternUiState.SaveSuccess) {
            // 保存画面へ遷移
            navController.navigate(Screen.SavePattern.createRoute(currentState.newFileUrl)) {
                // ConfirmPhoto画面までスタックをクリアする
                popUpTo(Screen.ConfirmPhoto.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターンを修正") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is EditEnglishPatternUiState.InitialLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EditEnglishPatternUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is EditEnglishPatternUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("OCRの解析結果", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = state.text,
                        onValueChange = { viewModel.onTextChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        label = { Text("編み方テキスト") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val csvString = viewModel.convertEnglishToCsvString()
                                // Base64エンコードして安全に渡す
                                val encodedContent = Base64.encodeToString(csvString.toByteArray(), Base64.NO_WRAP)
                                navController.navigate(Screen.EditOcrResult.createRoute(true, encodedContent))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("編み図に変換して修正")
                        }

                        Button(
                            onClick = { viewModel.saveEditedPattern(state.text) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("この内容で確定")
                        }
                    }
                }
            }
            is EditEnglishPatternUiState.Saving -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("保存中...")
                }
            }
            // SaveSuccess時はLaunchedEffectで遷移
            is EditEnglishPatternUiState.SaveSuccess -> {}
        }
    }
}