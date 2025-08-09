package com.example.kaleidoscopeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kaleidoscopeai.ui.screens.CubeScreen
import com.example.kaleidoscopeai.ui.screens.ChatScreen
import com.example.kaleidoscopeai.ui.screens.SettingsScreen
import com.example.kaleidoscopeai.ui.screens.MemoryScreen
import com.example.kaleidoscopeai.ui.viewmodel.KaleidoscopeViewModel
import com.example.kaleidoscopeai.ui.theme.KaleidoscopeAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaleidoscopeAITheme {
                val navController = rememberNavController()
                val viewModel: KaleidoscopeViewModel = hiltViewModel()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "cube",
                        modifier = androidx.compose.ui.Modifier.padding(it)
                    ) {
                        composable("cube") { CubeScreen(viewModel) }
                        composable("chat") { ChatScreen(viewModel) }
                        composable("settings") { SettingsScreen() }
                        composable("memory") { MemoryScreen(viewModel) }
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, val title: String, val icon: ImageVector)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        NavItem("cube", "Cube", Icons.Default.Home),
        NavItem("chat", "Chat", Icons.Default.Chat),
        NavItem("memory", "Memory", Icons.Default.List),
        NavItem("settings", "Settings", Icons.Default.Settings)
    )
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

object Icons {
    val Default = androidx.compose.material.icons.Icons.Default
}
