package com.example.kaleidoscopeai.core

import kotlin.math.sqrt

/**
 * Represents a 3D vector with common operations.
 */
data class Vec3(var x: Float, var y: Float, var z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = Vec3(x / scalar, y / scalar, z / scalar)

    fun length() = sqrt(x * x + y * y + z * z)
    fun normalize(): Vec3 {
        val len = length()
        return if (len > 0f) this / len else Vec3(0f, 0f, 0f)
    }

    fun distanceTo(other: Vec3) = (this - other).length()

    fun copy() = Vec3(x, y, z)
}
