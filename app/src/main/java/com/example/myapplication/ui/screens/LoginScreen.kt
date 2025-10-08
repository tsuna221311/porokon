package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.LoginViewModel

/**
 * ログイン画面のUIを担当するComposable。
 * この画面は、API通信などのロジックを一切含まず、
 * LoginViewModelから渡される状態(state)に応じて表示を切り替えることだけに専念します。
 *
 * @param state LoginViewModelが持つ現在のログイン状態。
 * @param onRetry ログインを再試行する（Firebase UIを表示する）ためのアクション。
 */
@Composable
fun LoginScreen(
    state: LoginViewModel.LoginState,
    onRetry: () -> Unit
) {
    // Failure時の自動再試行が1回だけ実行されるよう制御
    var retried by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is LoginViewModel.LoginState.Failure && !retried) {
            retried = true
            onRetry()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is LoginViewModel.LoginState.Initial -> {
                // 初期状態：ロゴやスプラッシュ表示なども可
                Text("アプリ起動中...", fontSize = 20.sp)
            }

            is LoginViewModel.LoginState.Checking -> {
                Text("ログイン状態を確認中...", fontSize = 20.sp)
            }

            is LoginViewModel.LoginState.ApiLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("…ユーザー情報を同期中…", fontSize = 20.sp)
                }
            }

            is LoginViewModel.LoginState.Success -> {
                Text("ログイン成功！", fontSize = 20.sp)
            }

            is LoginViewModel.LoginState.Failure -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ログイン失敗：${currentState.reason}", fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("再試行")
                    }
                }
            }
        }
    }
}