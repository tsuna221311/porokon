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
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object MyPatterns : Screen("my_patterns")
    object PatternView : Screen("pattern_view/{workId}") {
        fun createRoute(workId: Int) = "pattern_view/$workId"
    }

    // --- 新規作成フロー ---
    object SelectMode : Screen("select_mode")
    object CaptureChart : Screen("capture_chart")
    object CaptureEnglishPattern : Screen("capture_english_pattern")

    object ConfirmPhoto : Screen("confirm_photo/{isChart}/{photoUri}") {
        fun createRoute(isChart: Boolean, photoUri: String) = "confirm_photo/$isChart/$photoUri"
    }
    object EditOcrResult : Screen("edit_ocr_result/{isChart}/{csvUrl}") {
        fun createRoute(isChart: Boolean, csvUrl: String) = "edit_ocr_result/$isChart/$csvUrl"
    }
    object SavePattern : Screen("save_pattern/{fileUrl}") {
        fun createRoute(fileUrl: String) = "save_pattern/$fileUrl"
    }

    // --- その他 ---
    object EnglishPattern : Screen("english_pattern/{highlightedRow}") {
        fun createRoute(highlightedRow: Int) = "english_pattern/$highlightedRow"
    }
    object PatternEdit : Screen("pattern_edit/{workId}") {
        fun createRoute(workId: Int) = "pattern_edit/$workId"
    }
}

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
                // ★★★ 修正: 不足していたviewModelを渡す ★★★
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
            PatternDetailScreen(
                navController = navController,
                viewModel = viewModel()
            )
        }

        // --- 新規作成フロー ---
        composable(Screen.SelectMode.route) {
            SelectModeScreen(
                onNavigateToChartCapture = { navController.navigate(Screen.CaptureChart.route) },
                onNavigateToEnglishCapture = { navController.navigate(Screen.CaptureEnglishPattern.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CaptureChart.route) {
            CaptureChartScreen(navController = navController)
        }
        composable(Screen.CaptureEnglishPattern.route) {
            CaptureEnglishPatternScreen(navController = navController)
        }

        composable(
            route = Screen.ConfirmPhoto.route,
            arguments = listOf(
                navArgument("isChart") { type = NavType.BoolType },
                navArgument("photoUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ConfirmPhotoScreen(
                navController = navController,
                photoUri = backStackEntry.arguments?.getString("photoUri"),
                isChart = backStackEntry.arguments?.getBoolean("isChart") ?: false
            )
        }

        composable(
            route = Screen.EditOcrResult.route,
            arguments = listOf(
                navArgument("isChart") { type = NavType.BoolType },
                navArgument("csvUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isChart = backStackEntry.arguments?.getBoolean("isChart") ?: false
            if (isChart) {
                // ★★★ 修正: 不要な 'isNewPattern' 引数を削除 ★★★
                PatternEditScreen(navController = navController)
            } else {
                EditEnglishPatternScreen(navController = navController)
            }
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- その他 (既存作品の編集) ---
        composable(
            route = Screen.PatternEdit.route,
            arguments = listOf(navArgument("workId") { type = NavType.IntType })
        ) {
            // ★★★ 修正: 不要な 'isNewPattern' 引数を削除 ★★★
            PatternEditScreen(navController = navController)
        }

        composable(
            route = Screen.EnglishPattern.route,
            arguments = listOf(navArgument("highlightedRow") { type = NavType.IntType })
        ) {
            EnglishPatternScreen(navController = navController)
        }
    }
}
