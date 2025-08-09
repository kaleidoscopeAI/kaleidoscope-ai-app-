package com.example.kaleidoscopeai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaleidoscopeai.ui.viewmodel.KaleidoscopeViewModel

@Composable
fun MemoryScreen(viewModel: KaleidoscopeViewModel) {
    val allReflections by viewModel.memoryRepository.getAllReflections().collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Node Memory & Reflections")
        Spacer(modifier = Modifier.height(12.dp))
        allReflections.forEach { reflection ->
            Text("Node ${reflection.nodeId}: ${reflection.reflectionText}")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
