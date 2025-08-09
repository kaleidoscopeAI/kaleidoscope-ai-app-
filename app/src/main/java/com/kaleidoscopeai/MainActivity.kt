package com.kaleidoscopeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * MainActivity: Entry point for the app UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.kaleidoscopeai.ui.theme.KaleidoscopeTheme {
                val navController = androidx.navigation.compose.rememberNavController()
                val mainViewModel: com.kaleidoscopeai.viewmodel.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                androidx.compose.material3.Scaffold { innerPadding ->
                    androidx.navigation.compose.NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        androidx.navigation.compose.composable("main") {
                            com.kaleidoscopeai.ui.components.MainScreen(
                                viewModel = mainViewModel
                            )
                        }
                        androidx.navigation.compose.composable("analysis") {
                            com.kaleidoscopeai.ui.components.AnalysisTab()
                        }
                        androidx.navigation.compose.composable("research") {
                            com.kaleidoscopeai.ui.components.ResearchTab()
                        }
                        androidx.navigation.compose.composable("visualization") {
                            com.kaleidoscopeai.ui.components.VisualizationTab()
                        }
                        androidx.navigation.compose.composable("chat") {
                            com.kaleidoscopeai.ui.screens.ChatScreen(
                                messages = listOf("Welcome to the chat!"),
                                onSend = {}
                            )
                        }
                        androidx.navigation.compose.composable("cube") {
                            com.kaleidoscopeai.ui.screens.CubeScreen()
                        }
                        androidx.navigation.compose.composable("memory") {
                            com.kaleidoscopeai.ui.screens.MemoryScreen()
                        }
                        androidx.navigation.compose.composable("settings") {
                            com.kaleidoscopeai.ui.screens.SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}
