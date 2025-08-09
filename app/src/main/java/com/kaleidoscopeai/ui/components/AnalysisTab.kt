package com.kaleidoscopeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnalysisTab() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF181C24))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("System Analysis", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        // TODO: Add analytics, controls, and advanced system insights here
        Text("(Analysis features coming soon)", color = Color.White.copy(alpha = 0.6f))
    }
}
