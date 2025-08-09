package com.example.kaleidoscopeai.core

import kotlin.random.Random


import kotlin.math.*

class Node(
    val id: Int,
    var position: Vec3,
    val fixed: Boolean = false
) {
    // Physical and cognitive state variables
    var velocity = Vec3(0f, 0f, 0f)
    private var acceleration = Vec3(0f, 0f, 0f)
    var energy: Float = 1.0f
    var stress: Float = 0f
    var awareness: Float = 0f
    var memoryLoad: Float = 0f
    var knowledge: Float = 0f
    var emotion: Float = 0f
    var quantumState: FloatArray = FloatArray(2) { 0f } // Simplified quantum state
    var dna: Float = 1.0f
    var isFixed: Boolean = fixed
    var reflection: String? = null
    
    // Physics constants
    private val mass = 1.0f
    private val damping = 0.98f

    // Apply external force to the node
    fun applyForce(force: Vec3, deltaTime: Float) {
        if (!isFixed) {
            acceleration = acceleration + (force / mass)
        }
    }
    
    // Update node's physical position
    fun updatePosition(deltaTime: Float) {
        if (!isFixed) {
            // Verlet integration for stable physics
            velocity = velocity + (acceleration * deltaTime)
            velocity = velocity * damping // Apply damping
            position = position + (velocity * deltaTime)
            
            // Reset acceleration for next frame
            acceleration = Vec3(0f, 0f, 0f)
        }
    }

    // Update node state based on mathematical models
    fun updateState(neighbors: List<Node>, dt: Float = 1.0f) {
        // Energy dynamics: dE/dt = -C(stress) + R(recovery)
        val consumption = 0.1f * stress
        val recovery = 0.05f * (1f - energy)
        energy += (recovery - consumption) * dt
        energy = energy.coerceIn(0f, 1f)

        // Stress dynamics: SDE form
        val neighborStress = if (neighbors.isNotEmpty()) neighbors.map { it.stress }.average().toFloat() else 0f
        val alpha = 0.2f; val beta = 0.2f; val lambda = 0.1f
        val dStress = (-lambda * stress + alpha * memoryLoad + beta * (1f - energy)) * dt
        stress += dStress + 0.05f * (0.5f - Random.nextFloat()) // noise
        stress = stress.coerceIn(0f, 1f)

        // Knowledge & learning
        val learningRate = 0.1f * (1f - stress)
        val neighborKnowledge = if (neighbors.isNotEmpty()) neighbors.map { it.knowledge }.average().toFloat() else 0f
        knowledge += learningRate * (neighborKnowledge - knowledge) * dt
        knowledge = knowledge.coerceIn(0f, 1f)

        // Awareness (simplified)
        awareness = (energy + (1f - stress) + knowledge) / 3f

        // Emotion (feedback from stress and neighbors)
        val neighborEmotion = if (neighbors.isNotEmpty()) neighbors.map { it.emotion }.average().toFloat() else 0f
        emotion += 0.1f * (neighborEmotion - emotion) + 0.05f * (stress - 0.5f)
        emotion = emotion.coerceIn(-1f, 1f)

        // Quantum state (placeholder: random walk)
        quantumState[0] += 0.01f * (Random.nextFloat() - 0.5f)
        quantumState[1] += 0.01f * (Random.nextFloat() - 0.5f)
    }
    
    fun copy(): Node {
        val newNode = Node(id, position.copy(), fixed)
        newNode.velocity = velocity.copy()
        newNode.acceleration = acceleration.copy()
        newNode.energy = energy
        newNode.stress = stress
        newNode.awareness = awareness
        newNode.memoryLoad = memoryLoad
        newNode.knowledge = knowledge
        newNode.emotion = emotion
        newNode.quantumState = quantumState.copyOf()
        newNode.dna = dna
        newNode.isFixed = isFixed
        newNode.reflection = reflection
        return newNode
    }
}
