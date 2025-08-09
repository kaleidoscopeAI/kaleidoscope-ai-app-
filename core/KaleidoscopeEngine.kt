package com.example.kaleidoscopeai.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

class KaleidoscopeEngine(

    /**
     * Global state update: applies the meta-equation, updates all nodes, computes system metrics.
     * Optionally accepts external input (e.g., sensory data).
     */
    suspend fun globalStateUpdate(externalInput: Map<String, Any>? = null) {
        // Queue update for processing on physics thread
        physicsUpdateChannel.trySend(PhysicsUpdate(0.016f, externalInput))
    }
    
    private fun processGlobalStateUpdate(externalInput: Map<String, Any>? = null) {
        // 1. Routing: Build adjacency matrix for current bonds
        val n = nodes.size
        val routingMatrix = Array(n) { IntArray(n) { 0 } }
        bonds.filter { !it.isBroken() }.forEach { bond ->
            routingMatrix[bond.nodeA.id][bond.nodeB.id] = 1
            routingMatrix[bond.nodeB.id][bond.nodeA.id] = 1
        }

        // 2. Node operator application: update each node with its neighbors
        // Use parallel processing for large node sets
        if (nodes.size > 50) {
            nodes.chunked((nodes.size / 4).coerceAtLeast(1)).map { chunk ->
                coroutineScope.async(physicsDispatcher) {
                    chunk.forEach { node ->
                        val neighbors = bonds.filter { it.nodeA == node || it.nodeB == node }
                            .map { if (it.nodeA == node) it.nodeB else it.nodeA }
                        node.updateState(neighbors)
                    }
                }
            }.forEach { it.await() }
        } else {
            for (node in nodes) {
                val neighbors = bonds.filter { it.nodeA == node || it.nodeB == node }
                    .map { if (it.nodeA == node) it.nodeB else it.nodeA }
                node.updateState(neighbors)
            }
        }

        // 3. Tensor field update (placeholder: can be expanded for PDEs)
        // Example: tensorField = ...

        // 4. Topological & intelligence metrics (expensive - run periodically)
        if (frameCount % 60 == 0L) { // Every second at 60fps
            try {
                val analyzer = HomologicalAnalyzer()
                val result = analyzer.analyzeTopology(nodes, routingMatrix)
                lastHomologySignature = result.topologicalSignature
                lastIntelligenceMetrics = result.intelligenceMetrics
            } catch (e: Exception) {
                // Handle analysis errors gracefully
                println("Topology analysis error: ${e.message}")
            }
        }

        // 5. External input processing
        externalInput?.let { input ->
            processExternalInput(input)
        }
    }
    
    private fun processExternalInput(input: Map<String, Any>) {
        // Process external inputs safely
        input.forEach { (key, value) ->
            when (key) {
                "force" -> {
                    val forceData = value as? Map<String, Any>
                    val nodeId = forceData?.get("nodeId") as? Int
                    val force = forceData?.get("force") as? Vec3
                    if (nodeId != null && force != null) {
                        applyForceToNode(nodeId, force)
                    }
                }
                "energy" -> {
                    val energyValue = value as? Float ?: return@forEach
                    nodes.forEach { it.energy = (it.energy + energyValue * 0.1f).coerceIn(0f, 1f) }
                }
            }
        }
    }
    private val cubeSize: Int = 5,
    private val nodeSpacing: Float = 1.0f,
    private val coroutineScope: CoroutineScope
) {
    // System-level tensor field (for advanced field/tensor math)
    private var tensorField: Array<Array<Array<FloatArray>>>? = null
    // System-level metrics
    var lastHomologySignature: String? = null
    var lastIntelligenceMetrics: HomologicalAnalyzer.HomologyResult.IntelligenceMetrics? = null
    
    // Thread safety
    private val stateMutex = Mutex()
    private val physicsDispatcher = Dispatchers.Default.limitedParallelism(2)
    
    // Performance monitoring
    private var frameCount = 0L
    private var lastFrameTime = System.currentTimeMillis()
    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> get() = _performanceMetrics
    
    // Physics loop control
    private val physicsUpdateChannel = Channel<PhysicsUpdate>(Channel.UNLIMITED)
    private var isPhysicsActive = true
    
    data class PerformanceMetrics(
        val fps: Float = 0f,
        val frameTime: Long = 0L,
        val nodeCount: Int = 0,
        val bondCount: Int = 0,
        val activeBonds: Int = 0
    )
    
    data class PhysicsUpdate(
        val deltaTime: Float,
        val externalInput: Map<String, Any>? = null
    )
    private val nodes = mutableListOf<Node>()
    private val bonds = mutableListOf<Bond>()
    private val _nodesFlow = MutableStateFlow<List<Node>>(emptyList())
    val nodesFlow: StateFlow<List<Node>> get() = _nodesFlow
    private val _bondsFlow = MutableStateFlow<List<Bond>>(emptyList())
    val bondsFlow: StateFlow<List<Bond>> get() = _bondsFlow
    private val nodeMap = mutableMapOf<Int, Node>()

    init {
        initializeCube()
        startPhysicsLoop()
    }

    // Global state update: applies meta-equation and updates all nodes
    fun updateGlobalState(externalInputs: Map<String, Float> = emptyMap()) {
        // 1. Gather current state vector (all node states)
        val currentState = nodes.map { it.copy() }

        // 2. For each node, select relevant inputs (neighbors, fields, etc.)
        for (node in nodes) {
            val neighbors = getNeighbors(node)
            // 3. Apply node operator (updateState) with routing and external input
            node.updateState(neighbors)
        }

        // 4. Update tensor fields, metrics, etc. (placeholder for now)
        // TODO: Integrate tensor field and geometry updates

        // 5. Emit new state
        emitState()
    }

    // Helper to get direct neighbors of a node (by bond)
    private fun getNeighbors(node: Node): List<Node> {
        return bonds.filter { it.nodeA == node || it.nodeB == node }
            .map { if (it.nodeA == node) it.nodeB else it.nodeA }
    }

    private fun initializeCube() {
        var nodeId = 0
        for (x in 0 until cubeSize) {
            for (y in 0 until cubeSize) {
                for (z in 0 until cubeSize) {
                    val pos = Vec3(x * nodeSpacing, y * nodeSpacing, z * nodeSpacing)
                    val fixed = isOnBoundary(x, y, z)
                    val node = Node(nodeId, pos, fixed)
                    nodes.add(node)
                    nodeMap[nodeId] = node
                    nodeId++
                }
            }
        }
        var bondId = 0
        for (node in nodes) {
            val (x, y, z) = tripleFromId(node.id)
            val neighbors = listOf(
                nodeAt(x + 1, y, z), nodeAt(x, y + 1, z), nodeAt(x, y, z + 1)
            )
            neighbors.filterNotNull().forEach { neighbor ->
                val bond = Bond(bondId++, node, neighbor, node.position.distanceTo(neighbor.position))
                bonds.add(bond)
            }
        }
        emitState()
    }

    private fun isOnBoundary(x: Int, y: Int, z: Int): Boolean {
        return x == 0 || y == 0 || z == 0 ||
                x == cubeSize - 1 || y == cubeSize - 1 || z == cubeSize - 1
    }

    private fun nodeAt(x: Int, y: Int, z: Int): Node? {
        if (x !in 0 until cubeSize || y !in 0 until cubeSize || z !in 0 until cubeSize) return null
        val id = x * cubeSize * cubeSize + y * cubeSize + z
        return nodeMap[id]
    }

    private fun tripleFromId(id: Int): Triple<Int, Int, Int> {
        val x = id / (cubeSize * cubeSize)
        val y = (id % (cubeSize * cubeSize)) / cubeSize
        val z = id % cubeSize
        return Triple(x, y, z)
    }

    fun applyForceToNode(nodeId: Int, force: Vec3) {
        coroutineScope.launch(physicsDispatcher) {
            stateMutex.withLock {
                nodeMap[nodeId]?.applyForce(force, deltaTime = 0.016f)
            }
        }
    }
    
    fun pausePhysics() {
        isPhysicsActive = false
    }
    
    fun resumePhysics() {
        isPhysicsActive = true
        if (!isPhysicsActive) {
            startPhysicsLoop()
        }
    }
    
    fun shutdown() {
        isPhysicsActive = false
        physicsUpdateChannel.close()
    }

    private fun startPhysicsLoop() {
        // Physics computation on dedicated dispatcher
        coroutineScope.launch(physicsDispatcher) {
            val targetFrameTime = 16L // 60 FPS
            var lastUpdateTime = System.currentTimeMillis()
            
            while (isActive && isPhysicsActive) {
                val startTime = System.currentTimeMillis()
                val deltaTime = ((startTime - lastUpdateTime) / 1000f).coerceIn(0.001f, 0.033f)
                
                val frameTimeMs = measureTimeMillis {
                    stateMutex.withLock {
                        updatePhysicsStep(deltaTime)
                    }
                }
                
                // Update performance metrics
                updatePerformanceMetrics(frameTimeMs)
                
                // Emit state on Main dispatcher for UI updates
                withContext(Dispatchers.Main.immediate) {
                    emitState()
                }
                
                lastUpdateTime = startTime
                
                // Adaptive frame rate: skip frames if we're running slow
                val sleepTime = if (frameTimeMs < targetFrameTime) {
                    targetFrameTime - frameTimeMs
                } else {
                    1L // Minimal sleep to prevent thread starvation
                }
                
                delay(sleepTime)
            }
        }
        
        // Handle external updates on separate coroutine
        coroutineScope.launch(physicsDispatcher) {
            for (update in physicsUpdateChannel) {
                if (!isActive || !isPhysicsActive) break
                
                stateMutex.withLock {
                    processGlobalStateUpdate(update.externalInput)
                }
            }
        }
    }
    
    private fun updatePhysicsStep(deltaTime: Float) {
        updateBondTensions()
        propagateStress()
        updateNodePositions(deltaTime)
        validateBonds()
    }
    
    private fun updatePerformanceMetrics(frameTimeMs: Long) {
        frameCount++
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastFrameTime >= 1000) {
            val fps = frameCount.toFloat() / ((currentTime - lastFrameTime) / 1000f)
            val activeBonds = bonds.count { !it.isBroken() }
            
            _performanceMetrics.value = PerformanceMetrics(
                fps = fps,
                frameTime = frameTimeMs,
                nodeCount = nodes.size,
                bondCount = bonds.size,
                activeBonds = activeBonds
            )
            
            frameCount = 0
            lastFrameTime = currentTime
        }
    }

    private fun updateBondTensions() {
        bonds.forEach { bond ->
            if (!bond.isBroken()) bond.updateTension()
        }
    }

    private fun propagateStress() {
        bonds.forEach { bond ->
            if (bond.isBroken()) return@forEach
            val dir = (bond.nodeB.position - bond.nodeA.position).normalize()
            val forceVector = dir * bond.tension
            bond.nodeA.applyForce(forceVector * -1f, deltaTime = 0.016f)
            bond.nodeB.applyForce(forceVector, deltaTime = 0.016f)
        }
    }

    private fun updateNodePositions(deltaTime: Float) {
        nodes.forEach { it.updatePosition(deltaTime) }
    }

    private fun validateBonds() {
        bonds.forEach { it.validateBond() }
    }

    private fun emitState() {
        _nodesFlow.tryEmit(nodes.toList())
        _bondsFlow.tryEmit(bonds.toList())
    }

    fun injectReflection(nodeId: Int, reflection: String) {
        nodeMap[nodeId]?.reflection = reflection
    }
}
