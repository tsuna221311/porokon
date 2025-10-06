package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.ConfirmPhotoScreen
import com.example.myapplication.ui.screens.DashboardScreen
import com.example.myapplication.ui.screens.EnglishPatternScreen
import com.example.myapplication.ui.screens.MyPatternsScreen
import com.example.myapplication.ui.screens.OcrScreen
import com.example.myapplication.ui.screens.PatternDetailScreen
import com.example.myapplication.ui.screens.PatternEditScreen
import com.example.myapplication.ui.screens.SavePatternScreen
import com.example.myapplication.ui.screens.SelectModeScreen

/**
 * App's navigation routes, defined as a sealed class for type safety.
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object MyPatterns : Screen("my_patterns")
    object PatternView : Screen("pattern_view/{workId}") {
        fun createRoute(workId: Int) = "pattern_view/$workId"
    }
    object SelectMode : Screen("select_mode")
    object OcrCapture : Screen("ocr_capture")
    object ConfirmPhoto : Screen("confirm_photo")
    object SavePattern : Screen("save_pattern")
    object EnglishPattern : Screen("english_pattern")
    object PatternEdit : Screen("edit_pattern")
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

        // --- New Pattern Creation Flow ---
        composable(Screen.SelectMode.route) {
            SelectModeScreen(
                onNavigateToCamera = { navController.navigate(Screen.OcrCapture.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.OcrCapture.route) {
            OcrScreen(navController = navController)
        }
        composable(Screen.ConfirmPhoto.route) {
            ConfirmPhotoScreen(
                // Corrected: Use onConfirmClick parameter
                onConfirmClick = { navController.navigate(Screen.SavePattern.route) },
                onRetakeClick = { navController.popBackStack() }
            )
        }
        composable(Screen.SavePattern.route) {
            SavePatternScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Other Screens ---
        composable(Screen.EnglishPattern.route) {
            EnglishPatternScreen(navController = navController)
        }
        composable(Screen.PatternEdit.route) {
            PatternEditScreen(navController = navController)
        }
    }
}
