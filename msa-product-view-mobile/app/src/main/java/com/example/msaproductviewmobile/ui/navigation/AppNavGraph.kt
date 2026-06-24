package com.example.msaproductviewmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.msaproductviewmobile.ui.charts.ChartsScreen
import com.example.msaproductviewmobile.ui.charts.viewmodel.ChartsViewModel
import com.example.msaproductviewmobile.ui.data.DataScreen
import com.example.msaproductviewmobile.ui.settings.SettingsScreen

/**
 * App-wide navigation graph.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.CHARTS.route
    ) {
        // Charts Screen
        composable(AppRoute.CHARTS.route) {
            val viewModel: ChartsViewModel = hiltViewModel()
            ChartsScreen(
                viewModel = viewModel,
                onNavigateToData = {
                    navController.navigate(AppRoute.DATA.route)
                },
                onNavigateToSettings = {
                    navController.navigate(AppRoute.SETTINGS.route)
                }
            )
        }

        // Data Screen
        composable(AppRoute.DATA.route) {
            DataScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Data Detail Screen (with arguments)
        composable(
            route = AppRoute.DATA_DETAIL.route,
            arguments = listOf(
                navArgument(AppRoute.DATA_DETAIL.ITEM_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(AppRoute.DATA_DETAIL.ITEM_ID_ARG) ?: ""
            // DataDetailScreen(itemId = itemId, onNavigateBack = { navController.popBackStack() })
        }

        // Settings Screen
        composable(AppRoute.SETTINGS.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Navigation routes for the application.
 */
sealed class AppRoute(val route: String) {
    object CHARTS : AppRoute("charts")
    object DATA : AppRoute("data")
    object DATA_DETAIL : AppRoute("data_detail/{$itemId}") {
        const val ITEM_ID_ARG = "itemId"
    }
    object SETTINGS : AppRoute("settings")
}
