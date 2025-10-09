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
    val context = LocalContext.current
    var editedText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is EditEnglishPatternUiState.SaveSuccess -> {
                val encodedUrl = Uri.encode(state.newFileUrl)
                navController.navigate(Screen.SavePattern.createRoute(encodedUrl)) {
                    popUpTo(Screen.EditOcrResult.route) { inclusive = true }
                }
            }
            is EditEnglishPatternUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is EditEnglishPatternUiState.Success -> {
                if (editedText == null) {
                    editedText = state.text
                }
            }
            else -> {}
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
                is EditEnglishPatternUiState.InitialLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    val currentText = (state as? EditEnglishPatternUiState.Success)?.text
                    if (editedText == null && currentText != null) {
                        editedText = currentText
                    }

                    OutlinedTextField(
                        value = editedText ?: "",
                        onValueChange = {
                            editedText = it
                            viewModel.onTextChanged(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        label = { Text("OCRで読み取ったテキスト") },
                        enabled = state !is EditEnglishPatternUiState.Saving
                    )
                    Button(
                        onClick = { viewModel.saveEditedPattern(editedText ?: "") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state !is EditEnglishPatternUiState.Saving
                    ) {
                        if (state is EditEnglishPatternUiState.Saving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("この内容で確定する")
                        }
                    }
                }
            }
        }
    }
}