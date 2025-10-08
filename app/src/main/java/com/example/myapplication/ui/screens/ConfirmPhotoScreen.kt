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
import com.example.myapplication.ui.screens.RegisterWorkUiState.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPhotoScreen(
    navController: NavController,
    photoUri: String?,
    viewModel: RegisterWorkViewModel = viewModel()
) {
    val uri = remember(photoUri) { photoUri?.let { Uri.parse(it) } }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // uiState の変化を監視し、画面遷移やエラー表示を行う
    LaunchedEffect(uiState) {
        when (uiState) {
            is Success -> {
                Toast.makeText(context, "作品登録が完了しました！", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // 登録完了後に前の画面に戻る
            }
            is Error -> {
                val message = (uiState as Error).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            Loading, Standby -> {
                // Loading, Standby時は何もしない
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("写真の確認") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = "撮影した写真",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("写真がありません")
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
                    enabled = uiState !is Loading
                ) {
                    Text("撮り直す")
                }

                Button(
                    onClick = {
                        // OKボタンで作品登録
                        viewModel.registerNewWork(
                            title = "作品タイトル",
                            description = "作品説明"
                        )
                    },
                    enabled = uiState !is Loading
                ) {
                    if (uiState is Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("OK")
                    }
                }
            }
        }
    }
}