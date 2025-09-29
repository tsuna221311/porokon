package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Work
import com.example.myapplication.ui.components.common.PatternListItem
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.SecondarySalmon
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DashboardScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    dashboardUiState: StateFlow<DashboardUiState>
) {
    // 状態に応じて表示を切り替える
    val uiState by dashboardUiState.collectAsState()

    when (uiState) {
        is DashboardUiState.Loading -> LoadingScreen()
        is DashboardUiState.Success -> ResultScreen(
            navController = navController,
            onMenuClick = onMenuClick,
            works = (uiState as DashboardUiState.Success).works
        )
        is DashboardUiState.Error -> ErrorScreen()
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "エラーが発生しました", modifier = Modifier.padding(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    works: List<Work>?
) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                Toast.makeText(context, "写真が撮影されました", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "撮影がキャンセルされました", Toast.LENGTH_SHORT).show()
            }
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(context, "カメラの権限が拒否されました", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${works?.size ?: 0}件の作品") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                            cameraLauncher.launch(null)
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                containerColor = PrimaryTeal
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "カメラを起動", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(works.orEmpty()) { work ->
                PatternListItem(
                    title = work.title,
                    description = work.description,
                    icon = if (work.is_completed) Icons.Default.Check else Icons.Default.Edit,
                    iconColor = if (work.is_completed) SecondarySalmon else PrimaryTeal,
                    onClick = { navController.navigate(Routes.PATTERN_VIEW) }
                )
            }
        }
    }
}
