package com.example.myapplication.ui.navigation

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.components.drawer.AppDrawer
import com.example.myapplication.ui.screens.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val DASHBOARD = "dashboard"
    const val MY_PATTERNS = "my_patterns"
    const val PATTERN_VIEW = "pattern_view"
    const val PATTERN_DETAIL = "pattern_detail"
    const val ENGLISH_PATTERN = "english_pattern"
    const val PATTERN_EDIT = "pattern_edit" // 編み図修正画面のルートを追加
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onDestinationClicked = { route ->
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
            composable(Routes.DASHBOARD) {
                val dashboardViewModel: DashboardViewModel = viewModel()
                DashboardScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    dashboardUiState = dashboardViewModel.dashboardUiState,
                    )
            }
            composable(Routes.MY_PATTERNS) {
                MyPatternsScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.close() } }
                )
            }
            composable("${Routes.PATTERN_VIEW}/{workId}",
                arguments = listOf(navArgument("workId"){ type = NavType.IntType })
            ) { backStackEntry ->
                val patternViewModel: PatternViewModel = viewModel()
                PatternViewScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    viewModel = patternViewModel
                )
            }
            composable(Routes.PATTERN_DETAIL) {
                PatternDetailScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Routes.ENGLISH_PATTERN) {
                EnglishPatternScreen(navController = navController)
            }
            // 新しい編み図修正画面のルートをここに追加
            composable(Routes.PATTERN_EDIT) {
                PatternEditScreen(navController = navController)
            }
        }
    }
}

