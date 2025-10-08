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
    viewModel: OcrViewModel = viewModel()
) {
    // ナビゲーションから渡されたURI文字列をUriオブジェクトに変換
    val uri = remember(photoUri) { photoUri?.let { Uri.parse(it) } }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ViewModelの状態が変化したときの副作用（画面遷移やToast表示）を処理
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is OcrUiState.Success -> {
                Toast.makeText(context, "アップロード成功！", Toast.LENGTH_SHORT).show()
                viewModel.resetState() // 状態をリセット
                val encodedUrl = Uri.encode(state.fileUrl)
                // 成功したら、返ってきたURLを付けて作品保存画面へ遷移
                navController.navigate(Screen.SavePattern.createRoute(encodedUrl))
            }
            is OcrUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState() // 状態をリセット
            }
            else -> { /* Standby, Loading時には何もしない */ }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("写真の確認") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // 撮影された写真を画面に表示
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

            // 「撮り直す」と「OK」ボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 「撮り直す」ボタン
                Button(
                    onClick = { navController.popBackStack() },
                    enabled = uiState !is OcrUiState.Loading
                ) {
                    Text("撮り直す")
                }

                // 「OK」ボタン
                Button(
                    onClick = {
                        if (uri != null) {
                            // ViewModelに画像アップロードを依頼
                            viewModel.uploadImage(uri, context.contentResolver)
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
