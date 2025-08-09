package com.example.kaleidoscope.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kaleidoscope.app.viewmodel.MainViewModel
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var sceneView: SceneView? by remember { mutableStateOf(null) }
    val systemMetrics by viewModel.systemMetrics.collectAsState()
    val nodes by viewModel.nodes.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 3D Visualization
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    SceneView(context).apply {
                        sceneView = this
                    }
                },
                update = { view ->
                    // Update nodes in 3D space
                    nodes.forEach { node ->
                        val modelNode = view.findNodeByName(node.id) as? ModelNode
                            ?: ModelNode(view).apply {
                                name = node.id
                                view.addChildNode(this)
                            }
                        
                        modelNode.position = Position(
                            node.position.x.toFloat(),
                            node.position.y.toFloat(),
                            node.position.z.toFloat()
                        )
                        
                        // Update node appearance based on state
                        modelNode.scale = Position(
                            node.energy.toFloat() * 0.5f,
                            node.energy.toFloat() * 0.5f,
                            node.energy.toFloat() * 0.5f
                        )
                    }
                }
            )
        }

        // System Metrics
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "System Metrics",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                MetricRow("Consciousness", systemMetrics.consciousness)
                MetricRow("Harmony", systemMetrics.harmony)
                MetricRow("Global Entanglement", systemMetrics.globalEntanglement)
                MetricRow("Average Energy", systemMetrics.averageEnergy)
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.addNode() }) {
                Text("Add Node")
            }
            Button(onClick = { viewModel.clearNodes() }) {
                Text("Clear")
            }
            Button(onClick = { viewModel.toggleSimulation() }) {
                Text(if (viewModel.isSimulationRunning) "Pause" else "Start")
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(String.format("%.2f", value))
    }
}
