package com.terrariawiki.features.items.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.terrariawiki.features.items.domain.ItemCategory
import com.terrariawiki.features.items.ui.HomeScreen
import com.terrariawiki.features.items.ui.ItemDetailScreen
import com.terrariawiki.features.items.ui.ItemsByCategoryScreen

object ItemsRoutes {
    const val HOME = "home"
    const val CATEGORY = "category/{categoryId}"
    const val DETAIL = "item/{name}"

    fun category(category: ItemCategory): String = "category/${category.ordinal}"
    fun detail(name: String): String = "item/${java.net.URLEncoder.encode(name, "UTF-8")}"
}

fun NavGraphBuilder.itemsGraph(
    navController: NavController
) {
    composable(ItemsRoutes.HOME) {
        HomeScreen(
            onCategoryClick = { category ->
                navController.navigate(ItemsRoutes.category(category))
            }
        )
    }
    composable(
        route = ItemsRoutes.CATEGORY,
        arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
        val category = ItemCategory.fromOrdinalSafe(categoryId)
        ItemsByCategoryScreen(
            category = category,
            onItemClick = { name ->
                navController.navigate(ItemsRoutes.detail(name))
            },
            onBack = { navController.popBackStack() }
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
