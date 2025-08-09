package com.example.kaleidoscope.shared

import com.example.kaleidoscope.shared.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.random.Random

class KaleidoscopeAI {
    private val _nodes = mutableMapOf<String, Node>()
    private val _systemMetrics = MutableStateFlow(SystemMetrics())
    
    val systemMetrics: StateFlow<SystemMetrics> = _systemMetrics.asStateFlow()
    val nodes: Map<String, Node> get() = _nodes.toMap()

    fun createNode(position: Vector3D): String {
        val id = generateNodeId()
        _nodes[id] = Node(
            id = id,
            position = position
        )
        return id
    }

    fun updateSystem(dt: Double) {
        // Update node states
        _nodes.values.forEach { node ->
            val neighbors = node.connections
                .mapNotNull { _nodes[it] }
            node.updateEnergy(neighbors, dt)
        }

        // Update quantum states
        updateQuantumStates()

        // Calculate system-wide metrics
        updateSystemMetrics()
    }

    private fun updateQuantumStates() {
        _nodes.values.forEach { node ->
            node.quantumState.apply {
                phase = (phase + Random.nextDouble() * 2 * PI) % (2 * PI)
                amplitude *= 0.99 + Random.nextDouble() * 0.02
                entanglement = calculateEntanglement(node)
            }
        }
    }

    private fun calculateEntanglement(node: Node): Double {
        val neighbors = node.connections.mapNotNull { _nodes[it] }
        return if (neighbors.isEmpty()) 0.0 else {
            neighbors.sumOf { neighbor ->
                val phaseDiff = kotlin.math.abs(node.quantumState.phase - neighbor.quantumState.phase)
                kotlin.math.cos(phaseDiff) * node.quantumState.amplitude * neighbor.quantumState.amplitude
            } / neighbors.size
        }
    }

    private fun updateSystemMetrics() {
        val nodeCount = _nodes.size
        if (nodeCount == 0) return

        _systemMetrics.value = SystemMetrics(
            averageEnergy = _nodes.values.sumOf { it.energy } / nodeCount,
            averageStress = _nodes.values.sumOf { it.stress } / nodeCount,
            globalEntanglement = _nodes.values.sumOf { it.quantumState.entanglement } / nodeCount,
            consciousness = calculateGlobalConsciousness(),
            harmony = calculateGlobalHarmony()
        )
    }

    private fun calculateGlobalConsciousness(): Double {
        val nodeCount = _nodes.size
        if (nodeCount == 0) return 0.0

        val energyFactor = _nodes.values.sumOf { it.energy } / nodeCount
        val entanglementFactor = _nodes.values.sumOf { it.quantumState.entanglement } / nodeCount
        val complexityFactor = calculateComplexity()

        return (energyFactor * entanglementFactor * complexityFactor).coerceIn(0.0, 1.0)
    }

    private fun calculateGlobalHarmony(): Double {
        val nodeCount = _nodes.size
        if (nodeCount == 0) return 1.0

        val stressFactor = 1.0 - (_nodes.values.sumOf { it.stress } / nodeCount)
        val phaseCohesion = calculatePhaseCohesion()

        return (stressFactor * phaseCohesion).coerceIn(0.0, 1.0)
    }

    private fun calculateComplexity(): Double {
        val connections = _nodes.values.sumOf { it.connections.size }
        val maxPossibleConnections = _nodes.size * (_nodes.size - 1)
        return if (maxPossibleConnections == 0) 0.0 else connections.toDouble() / maxPossibleConnections
    }

    private fun calculatePhaseCohesion(): Double {
        val phases = _nodes.values.map { it.quantumState.phase }
        val averagePhase = phases.average()
        val phaseVariance = phases.sumOf { phase ->
            val diff = kotlin.math.abs(phase - averagePhase)
            kotlin.math.min(diff, 2 * PI - diff)
        } / phases.size
        return kotlin.math.exp(-phaseVariance)
    }

    private fun generateNodeId(): String = "node_${System.currentTimeMillis()}_${Random.nextInt(1000)}"
}

@Serializable
data class SystemMetrics(
    val averageEnergy: Double = 0.0,
    val averageStress: Double = 0.0,
    val globalEntanglement: Double = 0.0,
    val consciousness: Double = 0.0,
    val harmony: Double = 1.0
)
