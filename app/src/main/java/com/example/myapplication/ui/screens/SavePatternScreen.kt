package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePatternScreen(
    onSaveComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterWorkViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ViewModelの状態が変化したときに副作用（画面遷移やToast表示）を実行
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegisterWorkUiState.Success -> {
                Toast.makeText(context, "保存しました！", Toast.LENGTH_SHORT).show()
                onSaveComplete()
            }
            is RegisterWorkUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> { /* Standby, Loading時には何もしない */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("作品を保存") },
                navigationIcon = {
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
                .padding(16.dp)
        ) {
            Text(
                text = "新しい作品に名前をつけよう",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("タイトル") },
                singleLine = true,
                enabled = uiState !is RegisterWorkUiState.Loading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = memo,
                onValueChange = { memo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("メモ") },
                enabled = uiState !is RegisterWorkUiState.Loading
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.registerNewWork(title, memo)
                    } else {
                        Toast.makeText(context, "タイトルを入力してください", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                enabled = uiState !is RegisterWorkUiState.Loading
            ) {
                if (uiState is RegisterWorkUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("保存", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
