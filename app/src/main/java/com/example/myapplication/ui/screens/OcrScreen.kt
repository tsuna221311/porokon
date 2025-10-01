package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(
    navController: NavController,
    ocrViewModel: OcrViewModel = viewModel()
) {
    val uiState by ocrViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // カメラを起動するためのランチャー
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                ocrViewModel.onImageCaptured(imageUri)
            }
        }
    )

    // 写真を撮影するための関数
    fun takePhoto() {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        cameraLauncher.launch(imageUri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターンから作成") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // UIの状態に応じて表示を切り替える
            when (val state = uiState) {
                is OcrUiState.Idle -> {
                    Button(onClick = { takePhoto() }) {
                        Text("写真を撮る")
                    }
                }
                // ★★★ 新しく追加：写真確認UI ★★★
                is OcrUiState.ImageCaptured -> {
                    Text("この写真でよろしいですか？", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    // 撮影した写真をプレビュー表示
                    AsyncImage(
                        model = state.uri,
                        contentDescription = "撮影した写真",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { ocrViewModel.retakePhoto() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("撮り直す")
                        }
                        Button(
                            onClick = { ocrViewModel.processImageToText(state.uri) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("この写真を使う")
                        }
                    }
                }
                is OcrUiState.Processing -> {
                    CircularProgressIndicator()
                    Text("画像を解析中...", modifier = Modifier.padding(top = 16.dp))
                }
                is OcrUiState.TextExtracted -> {
                    // ... 変更なし ...
                }
                is OcrUiState.CsvGenerated -> {
                    // ... 変更なし ...
                }
                is OcrUiState.Error -> {
                    Text("エラー: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}



