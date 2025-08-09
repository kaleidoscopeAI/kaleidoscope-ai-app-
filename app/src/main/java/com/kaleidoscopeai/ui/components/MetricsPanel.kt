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
fun MetricsPanel(
    modifier: Modifier = Modifier,
    nodeCount: Int = 0,
    homologySignature: String? = null
) {
    Column(
        modifier
            .fillMaxHeight()
            .background(Color(0xFF232B3A))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("System Metrics", color = Color.White)
        Spacer(Modifier.height(12.dp))
        Text("- Node Count: $nodeCount", color = Color.White)
        Text("- Homology: ${homologySignature ?: "--"}", color = Color.White)
        // TODO: Add more live metrics as needed
    }
}
