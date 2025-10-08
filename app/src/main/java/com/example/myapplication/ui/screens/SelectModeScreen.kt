package com.example.myapplication.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.PrimaryTeal

/**
 * この画面は、API実装を前提とした完成されたUIコンポーネントです。
 * ダミーコードは含まれていません。
 *
 * 役割：
 * 1. ユーザーにスキャンモードの選択肢を提示する。
 * 2. 選択された結果や操作（「戻る」など）を、外部のナビゲーション担当に通知する。
 *
 * API通信などのロジックは、この画面からは分離されています。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectModeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新規作成") },
                navigationIcon = {
                    // 「戻る」ボタンが押されたことを外部に通知する
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "何を読み取りますか？",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(32.dp))

            ModeButton(
                title = "記号編み図をスキャン",
                description = "日本の編み図や手書きのチャートを読み取ります。",
                // ボタンが押されたことを外部に通知する
                onClick = onNavigateToCamera
            )
            Spacer(modifier = Modifier.height(16.dp))

            ModeButton(
                title = "英文パターンをスキャン",
                description = "海外の文章形式のパターンを読み取ります。",
                onClick = onNavigateToCamera
            )
        }
    }
}

@Composable
private fun ModeButton(title: String, description: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryTeal, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryTeal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
