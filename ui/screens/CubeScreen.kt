package com.example.kaleidoscopeai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kaleidoscopeai.core.Node
import com.example.kaleidoscopeai.core.Bond
import com.example.kaleidoscopeai.core.Vec3
import com.example.kaleidoscopeai.ui.viewmodel.KaleidoscopeViewModel

@Composable
fun CubeScreen(viewModel: KaleidoscopeViewModel) {
    val nodes by viewModel.nodesFlow.collectAsState()
    val bonds by viewModel.bondsFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kaleidoscope AI Cube Visualization")
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black)
        ) {
            CubeCanvas(nodes, bonds)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { /* TODO: Add force or relax action */ }) {
            Text("Relax Cube")
        }
    }
}

@Composable
fun CubeCanvas(nodes: List<Node>, bonds: List<Bond>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw bonds
        bonds.forEach { bond ->
            val a = bond.nodeA.position
            val b = bond.nodeB.position
            drawLine(
                color = Color.Cyan.copy(alpha = bond.validity),
                start = androidx.compose.ui.geometry.Offset(a.x * 40, a.y * 40),
                end = androidx.compose.ui.geometry.Offset(b.x * 40, b.y * 40),
                strokeWidth = 2f
            )
        }
        // Draw nodes
        nodes.forEach { node ->
            drawCircle(
                color = if (node.fixed) Color.Red else Color.Green,
                radius = 8f,
                center = androidx.compose.ui.geometry.Offset(node.position.x * 40, node.position.y * 40)
            )
        }
    }
}
