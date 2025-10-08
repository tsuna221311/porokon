package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Work
import com.example.myapplication.ui.components.common.PatternListItem
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.SecondarySalmon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPatternsScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    viewModel: MyPatternsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("マイ編み図") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        }
    ) { paddingValues ->
        // ViewModelの状態に応じてUIを切り替える
        when (val state = uiState) {
            is MyPatternsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MyPatternsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("データの読み込みに失敗しました。")
                }
            }
            is MyPatternsUiState.Success -> {
                if (state.works.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("作品がありません。")
                    }
                } else {
                    // 作品リストをLazyColumnで表示
                    LazyColumn(modifier = Modifier.padding(paddingValues)) {
                        items(state.works) { work ->
                            PatternListItem(
                                title = work.title,
                                description = work.description,
                                icon = if (work.is_completed) Icons.Default.Check else Icons.Default.Edit,
                                iconColor = if (work.is_completed) SecondarySalmon else PrimaryTeal,
                                onClick = {
                                    navController.navigate(Screen.PatternView.createRoute(work.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}