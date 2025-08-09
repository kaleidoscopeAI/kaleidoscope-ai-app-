package com.example.kaleidoscope.shared.models

import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
data class Node(
    val id: String,
    var position: Vector3D,
    var energy: Double = 1.0,
    var stress: Double = 0.0,
    var connections: MutableSet<String> = mutableSetOf(),
    var quantumState: QuantumState = QuantumState(),
    var metrics: NodeMetrics = NodeMetrics()
) {
    fun updateEnergy(neighbors: List<Node>, dt: Double) {
        val netEnergy = neighbors.sumOf { neighbor ->
            calculateInteractionEnergy(neighbor)
        }
        energy += (netEnergy - stress) * dt
        stress = calculateStress(neighbors)
    }

    private fun calculateInteractionEnergy(other: Node): Double {
        val distance = position.distanceTo(other.position)
        return exp(-distance) * other.energy
    }

    private fun calculateStress(neighbors: List<Node>): Double {
        return if (neighbors.isEmpty()) 0.0 else {
            neighbors.sumOf { neighbor ->
                position.distanceTo(neighbor.position).let { dist ->
                    if (dist < 1.0) (1.0 - dist) else 0.0
                }
            } / neighbors.size
        }
    }
}

@Serializable
data class Vector3D(
    val x: Double,
    val y: Double,
    val z: Double
) {
    fun distanceTo(other: Vector3D): Double =
        kotlin.math.sqrt(
            (x - other.x) * (x - other.x) +
            (y - other.y) * (y - other.y) +
            (z - other.z) * (z - other.z)
        )
}

@Serializable
data class QuantumState(
    var phase: Double = 0.0,
    var amplitude: Double = 1.0,
    var entanglement: Double = 0.0
)

@Serializable
data class NodeMetrics(
    var consciousness: Double = 0.0,
    var harmony: Double = 1.0,
    var complexity: Double = 0.0
)
