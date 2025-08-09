package com.kaleidoscopeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Cube3DRendererPlaceholder() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF232B3A)),
        contentAlignment = Alignment.Center
    ) {
        Text("3D Cube Renderer (OpenGL integration coming soon)", color = Color.White.copy(alpha = 0.7f))
    }
}
