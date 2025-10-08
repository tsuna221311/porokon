package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.*

/**
 * アプリ内の画面遷移ルートを定義する Sealed Class。
 * 文字列を直接使うよりもタイプミスを防げて安全です。
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object MyPatterns : Screen("my_patterns")
    object PatternView : Screen("pattern_view/{workId}") {
        fun createRoute(workId: Int) = "pattern_view/$workId"
    }
    object SelectMode : Screen("select_mode")
    object OcrCapture : Screen("ocr_capture")
    object ConfirmPhoto : Screen("confirm_photo/{photoUri}") {
        fun createRoute(photoUri: String) = "confirm_photo/$photoUri"
    }
    object SavePattern : Screen("save_pattern/{fileUrl}") {
        fun createRoute(fileUrl: String) = "save_pattern/$fileUrl"
    }
    // ★★★ 修正: highlightedRowを引数として受け取るように変更 ★★★
    object EnglishPattern : Screen("english_pattern/{highlightedRow}") {
        fun createRoute(highlightedRow: Int) = "english_pattern/$highlightedRow"
    }
    object PatternEdit : Screen("pattern_edit/{workId}") {
        fun createRoute(workId: Int) = "pattern_edit/$workId"
    }
}

/**
 * アプリ全体のナビゲーショングラフを定義するComposable。
 * @param navController アプリケーションのナビゲーションを制御します。
 * @param onMenuClick サイドメニューを開くためのアクションを伝達します。
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                onMenuClick = onMenuClick,
                dashboardViewModel = viewModel()
            )
        }

        composable(Screen.MyPatterns.route) {
            MyPatternsScreen(
                navController = navController,
                onMenuClick = onMenuClick
            )
        }

        composable(
            route = Screen.PatternView.route,
            arguments = listOf(navArgument("workId") { type = NavType.IntType })
        ) {
            // ダミーデータを表示するバージョンのPatternViewScreenを呼び出す
            PatternViewScreen(
                navController = navController
            )
        }

        // --- 新規作成フロー ---
        composable(Screen.SelectMode.route) {
            SelectModeScreen(
                onNavigateToCamera = { navController.navigate(Screen.OcrCapture.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.OcrCapture.route) {
            OcrScreen(navController = navController)
        }
        composable(
            route = Screen.ConfirmPhoto.route,
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoUri = backStackEntry.arguments?.getString("photoUri")
            ConfirmPhotoScreen(
                navController = navController,
                photoUri = photoUri
            )
        }
        composable(
            route = Screen.SavePattern.route,
            arguments = listOf(navArgument("fileUrl") { type = NavType.StringType })
        ) {
            SavePatternScreen(
                onSaveComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- その他 ---
        // ★★★ 修正: EnglishPatternのルート定義に引数を追加 ★★★
        composable(
            route = Screen.EnglishPattern.route,
            arguments = listOf(navArgument("highlightedRow") { type = NavType.IntType })
        ) {
            EnglishPatternScreen(navController = navController)
        }
        composable(
            route = Screen.PatternEdit.route,
            arguments = listOf(navArgument("workId") { type = NavType.IntType })
        ) {
            PatternEditScreen(navController = navController)
        }
    }
}

