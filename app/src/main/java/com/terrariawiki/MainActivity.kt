package com.terrariawiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.terrariawiki.core.ui.theme.TerrariaWikiTheme
import com.terrariawiki.features.items.ui.ItemDetailScreen
import com.terrariawiki.features.items.ui.ItemsScreen
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                TerrariaWikiTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        TerrariaWikiNavHost()
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun TerrariaWikiNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "items"
    ) {
        composable("items") {
            ItemsScreen(
                onItemClick = { name ->
                    navController.navigate("item/${java.net.URLEncoder.encode(name, "UTF-8")}")
                }
            )
        }
        composable(
            route = "item/{name}",
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
}
