package com.example.kaleidoscopeai.core

import com.example.kaleidoscopeai.core.HomologicalAnalyzer
import com.example.kaleidoscopeai.core.Node
import com.example.kaleidoscopeai.core.Vec3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@ExperimentalCoroutinesApi
class KaleidoscopeEngineTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)
    private lateinit var kaleidoscopeEngine: KaleidoscopeEngine

    @Before
    fun setUp() {
        kaleidoscopeEngine = KaleidoscopeEngine(coroutineScope = testScope)
    }

    @After
    fun tearDown() {
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `test cube initialization`() {
        val nodes = kaleidoscopeEngine.nodesFlow.value
        val bonds = kaleidoscopeEngine.bondsFlow.value
        assertEquals(125, nodes.size)
        assertEquals(300, bonds.size)
    }

    @Test
    fun `test global state update`() {
        kaleidoscopeEngine.globalStateUpdate()
        assertNotNull(kaleidoscopeEngine.lastHomologySignature)
        assertNotNull(kaleidoscopeEngine.lastIntelligenceMetrics)
    }

    @Test
    fun `test apply force to node`() {
        val nodeId = 0
        val force = Vec3(1f, 2f, 3f)
        kaleidoscopeEngine.applyForceToNode(nodeId, force)
        val node = kaleidoscopeEngine.nodesFlow.value.find { it.id == nodeId }
        assertNotNull(node)
        assertEquals(force, node!!.velocity)
    }

    @Test
    fun `test inject reflection to node`() {
        val nodeId = 0
        val reflection = "test reflection"
        kaleidoscopeEngine.injectReflection(nodeId, reflection)
        val node = kaleidoscopeEngine.nodesFlow.value.find { it.id == nodeId }
        assertNotNull(node)
        assertEquals(reflection, node!!.reflection)
    }

    @Test
    fun `test physics loop`() = testScope.runBlockingTest {
        val nodeId = 0
        val initialPosition = kaleidoscopeEngine.nodesFlow.value.find { it.id == nodeId }!!.position
        advanceTimeBy(100)
        val newPosition = kaleidoscopeEngine.nodesFlow.value.find { it.id == nodeId }!!.position
        assert(initialPosition != newPosition)
    }
