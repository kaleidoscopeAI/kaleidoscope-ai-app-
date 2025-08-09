package com.example.kaleidoscopeai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings")
        Spacer(modifier = Modifier.height(12.dp))
        Text(" - Ollama API settings (base URL, model)")
        Text(" - Web crawler config")
        Text(" - Export options (PDF/CSV)")
        Text(" - Cube simulation parameters")
    }
}
