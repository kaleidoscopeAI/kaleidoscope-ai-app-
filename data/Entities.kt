package com.example.kaleidoscopeai.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kaleidoscopeai.core.Vec3

@Entity(tableName = "node_reflections")
data class NodeReflection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nodeId: Int,
    val timestamp: Long,
    val reflectionText: String
)

@Entity(tableName = "node_states")
data class NodeStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nodeId: Int,
    val positionX: Float,
    val positionY: Float,
    val positionZ: Float,
    val velocityX: Float,
    val velocityY: Float,
    val velocityZ: Float,
    val energy: Float,
    val tension: Float,
    val reflection: String? = null,
    val timestamp: Long
)

@Entity(tableName = "bond_states")
data class BondStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bondId: Int,
    val nodeAId: Int,
    val nodeBId: Int,
    val tension: Float,
    val validity: Float,
    val timestamp: Long
)
