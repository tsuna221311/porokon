package com.example.myapplication.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.AppDrawer
import com.example.myapplication.DashboardScreen
import com.example.myapplication.MyPatternsScreen
import com.example.myapplication.PatternDetailScreen
import com.example.myapplication.PatternViewScreen
import com.example.myapplication.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val DASHBOARD = "dashboard"
    const val MY_PATTERNS = "my_patterns"
    const val PATTERN_VIEW = "pattern_view"
    const val PATTERN_DETAIL = "pattern_detail"
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
                DashboardScreen(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
            composable(Routes.MY_PATTERNS) {
                MyPatternsScreen(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
            composable(Routes.PATTERN_VIEW) {
                PatternViewScreen(onMenuClick = { scope.launch { drawerState.open() } })
            }
            composable(Routes.PATTERN_DETAIL) {
                PatternDetailScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}