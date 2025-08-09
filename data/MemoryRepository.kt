package com.example.kaleidoscopeai.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryRepository(
    private val db: KaleidoscopeDatabase
) {
    suspend fun saveNodeReflection(reflection: NodeReflection) =
        withContext(Dispatchers.IO) { db.nodeReflectionDao().insertReflection(reflection) }

    fun getNodeReflections(nodeId: Int): Flow<List<NodeReflection>> =
        db.nodeReflectionDao().getReflectionsForNode(nodeId)

    fun getAllReflections(): Flow<List<NodeReflection>> =
        db.nodeReflectionDao().getAllReflections()

    suspend fun saveNodeState(state: NodeStateEntity) =
        withContext(Dispatchers.IO) { db.nodeStateDao().insertNodeState(state) }

    suspend fun getLatestNodeState(nodeId: Int): NodeStateEntity? =
        withContext(Dispatchers.IO) { db.nodeStateDao().getLatestStateForNode(nodeId) }

    suspend fun saveBondState(state: BondStateEntity) =
        withContext(Dispatchers.IO) { db.bondStateDao().insertBondState(state) }

    suspend fun getLatestBondState(bondId: Int): BondStateEntity? =
        withContext(Dispatchers.IO) { db.bondStateDao().getLatestStateForBond(bondId) }
}
