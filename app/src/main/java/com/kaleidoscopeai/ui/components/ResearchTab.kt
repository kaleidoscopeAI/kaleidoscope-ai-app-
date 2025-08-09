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
fun ResearchTab() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF181C24))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Research & Documentation", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        // TODO: Add research tools, document viewers, and knowledge base here
        Text("(Research features coming soon)", color = Color.White.copy(alpha = 0.6f))
    }
}
