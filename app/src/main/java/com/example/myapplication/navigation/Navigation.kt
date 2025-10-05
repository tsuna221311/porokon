package com.example.myapplication.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
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
    // ... 他のルート
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { /* ... */ }
    ) {
        NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
            composable(Routes.DASHBOARD) {
                val dashboardViewModel: DashboardViewModel = viewModel()
                DashboardScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    dashboardViewModel = dashboardViewModel
                )
            }
            composable(Routes.MY_PATTERNS) {
                MyPatternsScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.close() } }
                )
            }
            composable(
                route = "${Routes.PATTERN_VIEW}/{workId}",
                arguments = listOf(navArgument("workId") { type = NavType.IntType })
            ) {
                val patternViewModel: PatternViewModel = viewModel()
                PatternViewScreen(
                    navController = navController,
                    viewModel = patternViewModel
                )
            }
            // ★★★ 修正箇所：この呼び出しが、修正後のPatternDetailScreenと一致します ★★★
            composable(Routes.PATTERN_DETAIL) {
                PatternDetailScreen(onBackClick = { navController.popBackStack() })
            }
            // ... 他の画面
        }
    }
}
