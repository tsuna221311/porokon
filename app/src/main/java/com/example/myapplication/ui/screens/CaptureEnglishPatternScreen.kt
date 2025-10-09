package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen

/**
 * 英文パターンを撮影するための画面。
 * カメラの権限を確認し、共通のCameraCaptureコンポーネントを呼び出します。
 */
@Composable
fun CaptureEnglishPatternScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCamPermission = granted }
    )

    // 画面が表示されたときに、カメラ権限がなければ要求する
    LaunchedEffect(key1 = true) {
        if (!hasCamPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCamPermission) {
        // 権限があれば、共通のカメラUIを呼び出す
        CameraCapture(
            onImageCaptured = { uri ->
                // 撮影成功時、isChart=false を付けて確認画面へ遷移
                val encodedUri = Uri.encode(uri.toString())
                navController.navigate(Screen.ConfirmPhoto.createRoute(false, encodedUri))
            },
            onClose = { navController.popBackStack() }
        )
    } else {
        // 権限がなければメッセージを表示
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("カメラの権限が必要です。")
        }
    }
}

