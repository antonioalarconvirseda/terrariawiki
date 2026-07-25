package com.terrariawiki.features.items.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.terrariawiki.features.items.ui.ItemDetailScreen
import com.terrariawiki.features.items.ui.ItemsScreen

object ItemsRoutes {
    const val LIST = "items"
    const val DETAIL = "item/{name}"
    fun detail(name: String): String = "item/${java.net.URLEncoder.encode(name, "UTF-8")}"
}

fun NavGraphBuilder.itemsGraph(
    navController: NavController
) {
    composable(ItemsRoutes.LIST) {
        ItemsScreen(
            onItemClick = { name ->
                navController.navigate(ItemsRoutes.detail(name))
            }
        )
    }
    composable(
        route = ItemsRoutes.DETAIL,
        arguments = listOf(navArgument("name") { type = NavType.StringType })
    ) { backStackEntry ->
        val encoded = backStackEntry.arguments?.getString("name").orEmpty()
        val name = java.net.URLDecoder.decode(encoded, "UTF-8")
        ItemDetailScreen(
            name = name,
            onBack = { navController.popBackStack() }
        )
    }
}
