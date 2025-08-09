package com.example.kaleidoscopeai.core

import kotlin.math.abs
import kotlin.random.Random

class Bond(
    val id: Int,
    val nodeA: Node,
    val nodeB: Node,
    val restingLength: Float,
    val stiffness: Float = 5.0f
) {
    var tension: Float = 0f
    var validity: Float = 1.0f

    fun updateTension() {
        val currentLength = nodeA.position.distanceTo(nodeB.position)
        val delta = currentLength - restingLength
        tension = delta * stiffness
    }

    fun validateBond() {
        val tensionAbs = abs(tension)
        if (tensionAbs > restingLength * 0.2f) {
            val decayChance = (tensionAbs / (restingLength * 2f)).coerceIn(0f, 1f)
            if (Random.nextFloat() < decayChance) {
                validity = (validity - 0.05f).coerceAtLeast(0f)
            }
        } else {
            validity = (validity + 0.02f).coerceAtMost(1f)
        }
    }

    fun isBroken() = validity <= 0f
}
