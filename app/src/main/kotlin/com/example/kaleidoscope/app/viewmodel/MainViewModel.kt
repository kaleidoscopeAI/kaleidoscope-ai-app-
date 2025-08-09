package com.example.kaleidoscope.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaleidoscope.shared.KaleidoscopeAI
import com.example.kaleidoscope.shared.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val kaleidoscopeAI = KaleidoscopeAI()
    private var simulationJob: Job? = null

    private val _nodes = MutableStateFlow<List<Node>>(emptyList())
    val nodes: StateFlow<List<Node>> = _nodes.asStateFlow()

    val systemMetrics = kaleidoscopeAI.systemMetrics
    var isSimulationRunning = false
        private set

    init {
        // Start collecting nodes
        viewModelScope.launch {
            while (isActive) {
                _nodes.value = kaleidoscopeAI.nodes.values.toList()
                delay(16) // ~60 FPS
            }
        }
    }

    fun addNode() {
        val position = Vector3D(
            x = Random.nextDouble(-5.0, 5.0),
            y = Random.nextDouble(-5.0, 5.0),
            z = Random.nextDouble(-5.0, 5.0)
        )
        kaleidoscopeAI.createNode(position)
    }

    fun clearNodes() {
        viewModelScope.launch {
            stopSimulation()
            _nodes.value = emptyList()
        }
    }

    fun toggleSimulation() {
        if (isSimulationRunning) {
            stopSimulation()
        } else {
            startSimulation()
        }
    }

    private fun startSimulation() {
        if (!isSimulationRunning) {
            isSimulationRunning = true
            simulationJob = viewModelScope.launch {
                while (isActive) {
                    kaleidoscopeAI.updateSystem(0.016) // ~60 FPS
                    delay(16)
                }
            }
        }
    }

    private fun stopSimulation() {
        isSimulationRunning = false
        simulationJob?.cancel()
        simulationJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
    }
}
