package core

import org.junit.Assert.assertEquals
import org.junit.Test

class HomologicalAnalyzerTest {

    private val analyzer = HomologicalAnalyzer()

    @Test
    fun `test empty graph`() {
        val result = analyzer.analyzeTopology(emptyList(), emptyArray())
        assertEquals("0-0-0-0", result.topologicalSignature)
    }

    @Test
    fun `test single node`() {
        val result = analyzer.analyzeTopology(listOf(0), arrayOf(intArrayOf(0)))
        assertEquals("1-0-0-1", result.topologicalSignature)
    }

    @Test
    fun `test two connected nodes`() {
        val routingMatrix = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 0)
        )
        val result = analyzer.analyzeTopology(listOf(0, 1), routingMatrix)
        assertEquals("1-0-0-1", result.topologicalSignature)
    }

    @Test
    fun `test two disconnected nodes`() {
        val routingMatrix = arrayOf(
            intArrayOf(0, 0),
            intArrayOf(0, 0)
        )
        val result = analyzer.analyzeTopology(listOf(0, 1), routingMatrix)
        assertEquals("2-0-0-2", result.topologicalSignature)
    }

    @Test
    fun `test cyclic graph`() {
        val routingMatrix = arrayOf(
            intArrayOf(0, 1, 0, 1),
            intArrayOf(1, 0, 1, 0),
            intArrayOf(0, 1, 0, 1),
            intArrayOf(1, 0, 1, 0)
        )
        val result = analyzer.analyzeTopology(listOf(0, 1, 2, 3), routingMatrix)
        assertEquals("1-1-0-2", result.topologicalSignature)
    }

    @Test
    fun `test graph with triangles`() {
        val routingMatrix = arrayOf(
            intArrayOf(0, 1, 1),
            intArrayOf(1, 0, 1),
            intArrayOf(1, 1, 0)
        )
        val result = analyzer.analyzeTopology(listOf(0, 1, 2), routingMatrix)
        assertEquals("1-1-1-3", result.topologicalSignature)
    }

    @Test
    fun `test empty analysis`() {
        val result = analyzer.emptyAnalysis()
        assertEquals("0-0-0-0", result.topologicalSignature)
    }
}
