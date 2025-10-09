package com.example.myapplication.ui.screens

import android.net.Uri
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
import com.example.myapplication.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPhotoScreen(
    navController: NavController,
    photoUri: String?,
    isChart: Boolean,
    viewModel: OcrViewModel = viewModel()
) {
    val uri = remember(photoUri) { photoUri?.let { Uri.parse(it) } }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ViewModelの状態が変化したときの副作用（画面遷移やToast表示）を処理
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            // ★★★ 修正: 成功状態を 'Success' 一つでハンドリング ★★★
            is OcrUiState.Success -> {
                Toast.makeText(context, "解析成功！", Toast.LENGTH_SHORT).show()
                viewModel.resetState()

                // isChartフラグと、解析結果のテキストを次の画面に渡す
                val encodedContent = Uri.encode(state.initialContent)
                navController.navigate(Screen.EditOcrResult.createRoute(state.isChart, encodedContent)) {
                    popUpTo(Screen.ConfirmPhoto.route) { inclusive = true }
                }
            }
            is OcrUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> { /* Standby, Loading時には何もしない */ }
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
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("画像の読み込みに失敗しました。")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    enabled = uiState !is OcrUiState.Loading
                ) {
                    Text("撮り直す")
                }

                Button(
                    onClick = {
                        if (uri != null) {
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
