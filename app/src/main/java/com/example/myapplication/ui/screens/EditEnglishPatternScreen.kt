package com.example.myapplication.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    // 編集中のテキストを保持する状態。初期値はnull。
    var editedText by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // uiStateがSuccessの場合に、そのテキストをeditedTextに一度だけ設定する
    LaunchedEffect(uiState) {
        if (uiState is EditEnglishPatternUiState.Success && editedText == null) {
            editedText = (uiState as EditEnglishPatternUiState.Success).text
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターンの確認・修正") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is EditEnglishPatternUiState.Success -> {
                    OutlinedTextField(
                        value = editedText ?: "",
                        onValueChange = {
                            editedText = it
                            // テキストが変更されるたびにViewModelの状態も更新
                            viewModel.onTextChanged(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        label = { Text("OCRで読み取ったテキスト") }
                    )
                    Button(
                        onClick = {
                            // ViewModelから最新のCSV文字列を取得
                            val csvContent = viewModel.convertEnglishToCsvString()
                            // ★★★ 修正点1: 空白でないかをチェック ★★★
                            if (csvContent.isNotBlank()) {
                                // データをエンコードして次の画面に渡す
                                val encodedCsv = Uri.encode(csvContent)
                                navController.navigate(Screen.EditOcrResult.createRoute(true, encodedCsv))
                            } else {
                                // ★★★ 修正点2: データが空の場合にユーザーに通知 ★★★
                                Toast.makeText(context, "テキストが空です。何か入力してください。", Toast.LENGTH_SHORT).show()
                            }
                        },
                        // ★★★ 改善点: テキストが入力されていない場合はボタンを無効化 ★★★
                        enabled = !editedText.isNullOrBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("編み図に変換して修正する")
                    }
                }
                is EditEnglishPatternUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message)
                    }
                }
                is EditEnglishPatternUiState.InitialLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // 以下は網羅性のための記述
                is EditEnglishPatternUiState.Saving -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is EditEnglishPatternUiState.SaveSuccess -> {
                    // この画面では基本起こらないが念のため
                }
            }
        }
    }
}
