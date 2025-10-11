package com.example.myapplication.ui.screens

import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.data.dummyEnglishPatternInstructions
import com.example.myapplication.data.dummyPatternFromImage
import com.example.myapplication.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPhotoScreen(
    navController: NavController,
    photoUri: String?,
    isChart: Boolean, // trueなら編み図カメラ、falseなら英文カメラ
    viewModel: OcrViewModel = viewModel()
) {
    val uri = remember(photoUri) { photoUri?.let { Uri.parse(it) } }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        val currentState = uiState
        if (currentState is OcrUiState.Success) {
            // OCRは成功したが、結果は無視してモードに応じたダミーデータを使用する
            val finalContent: String
            val navigateToChartEditor: Boolean

            if (isChart) {
                // 編み図カメラの場合、グリッドのダミーデータをCSV文字列に変換
                finalContent = dummyPatternFromImage.joinToString("\n") { it.joinToString(",") }
                navigateToChartEditor = true // → PatternEditScreen (グリッド編集画面) へ
            } else {
                // 英文カメラの場合、英文のダミーデータを改行区切りの1つの文字列に変換
                finalContent = dummyEnglishPatternInstructions.joinToString("\n")
                navigateToChartEditor = false // → EditEnglishPatternScreen (テキスト編集画面) へ
            }

            // Base64エンコードして次の画面に安全に渡す
            val encodedContent = Base64.encodeToString(finalContent.toByteArray(), Base64.NO_WRAP)

            // isChartフラグを次の画面に渡して、遷移先を決定する
            navController.navigate(Screen.EditOcrResult.createRoute(navigateToChartEditor, encodedContent)) {
                popUpTo(Screen.ConfirmPhoto.route) { inclusive = true }
            }
        } else if (currentState is OcrUiState.Error) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("写真の確認") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "撮影した写真",
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("画像の読み込みに失敗しました。")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { navController.popBackStack() }, enabled = uiState !is OcrUiState.Loading) {
                    Text("撮り直す")
                }
                Button(
                    onClick = {
                        if (uri != null) {
                            // OCR処理はトリガーするが、結果は使わない
                            viewModel.uploadImage(uri, context.contentResolver, isChart)
                        } else {
                            Toast.makeText(context, "画像がありません。", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = uiState !is OcrUiState.Loading && uri != null
                ) {
                    if (uiState is OcrUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("OK")
                    }
                }
            }
        }
    }
}

