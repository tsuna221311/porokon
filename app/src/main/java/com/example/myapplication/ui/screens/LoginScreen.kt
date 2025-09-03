package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    state: LoginViewModel.LoginState,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is LoginViewModel.LoginState.Checking -> Text("ログイン状態を確認中...", fontSize = 20.sp)
            is LoginViewModel.LoginState.ApiLoading -> Text("…ユーザー検証中…", fontSize = 20.sp)
            is LoginViewModel.LoginState.Success -> Text("ログイン成功！UID: ${state.uid}", fontSize = 20.sp)
            is LoginViewModel.LoginState.Failure -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ログイン失敗：${state.reason}", fontSize = 20.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("再試行")
                }
            }
        }
    }
}
