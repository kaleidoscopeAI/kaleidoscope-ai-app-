package com.example.kaleidoscopeai.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeReflectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: NodeReflection)

    @Query("SELECT * FROM node_reflections WHERE nodeId = :nodeId ORDER BY timestamp DESC")
    fun getReflectionsForNode(nodeId: Int): Flow<List<NodeReflection>>

    @Query("SELECT * FROM node_reflections ORDER BY timestamp DESC")
    fun getAllReflections(): Flow<List<NodeReflection>>
}

@Dao
interface NodeStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodeState(state: NodeStateEntity)

    @Query("SELECT * FROM node_states WHERE nodeId = :nodeId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestStateForNode(nodeId: Int): NodeStateEntity?
}

@Dao
interface BondStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBondState(state: BondStateEntity)

    @Query("SELECT * FROM bond_states WHERE bondId = :bondId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestStateForBond(bondId: Int): BondStateEntity?
}
