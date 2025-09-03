package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Work
import com.example.myapplication.navigation.Routes
import com.example.myapplication.ui.components.common.PatternListItem
import com.example.myapplication.ui.theme.AmuNaviTheme
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.SecondarySalmon

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DashboardScreen(
    dashboardUiState: DashboardUiState,
    modifier: Modifier = Modifier,
    navController: NavController,
    onMenuClick: () -> Unit
) {
    when (dashboardUiState) {
        is DashboardUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is DashboardUiState.Success -> ResultScreen(navController,onMenuClick, dashboardUiState.works)

        is DashboardUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Text(text = "ローディング中", modifier = Modifier.padding(16.dp))
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "エラーです", modifier = Modifier.padding(16.dp))
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
//                title = { Text("ダッシュボード") },
                title = { Text("${works?.size ?: 0}件の作品を取得") },
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
            item {
                PatternListItem(
                    title = "シンプルなマフラー",
                    description = "最近の作業: 5段目を編み終えました",
                    icon = Icons.Default.Edit,
                    iconColor = PrimaryTeal,
                    onClick = { navController.navigate(Routes.PATTERN_VIEW) }
                )
            }
            item {
                PatternListItem(
                    title = "ハートのコースター",
                    description = "完了！ - 2024/02/01",
                    icon = Icons.Default.Check,
                    iconColor = SecondarySalmon,
                    onClick = { /* 詳細画面などへ */ }
                )
            }
        }
    }
}