package core

/**
 * HomologicalAnalyzer: Performs topological analysis (H0, H1, H2) and intelligence metrics on node networks.
 * Ported and extended from the JavaScript reference implementation.
 */
class HomologicalAnalyzer {
    data class HomologyResult(
        val homology: Homology,
        val intelligenceMetrics: IntelligenceMetrics,
        val topologicalSignature: String
    ) {
        data class Homology(val H0_rank: Int, val H1_rank: Int, val H2_rank: Int)
        data class IntelligenceMetrics(
            val informationComponents: Int,
            val informationCycles: Int,
            val higherOrderStructures: Int,
            val topologicalComplexity: Int,
            val networkConnectivity: NetworkConnectivity
        ) {
            data class NetworkConnectivity(val connected: Boolean)
        }
    }

    /**
     * Analyze the topology of a network given nodes and an adjacency matrix.
     * @param nodes List of node IDs or objects.
     * @param routingMatrix Adjacency matrix (Boolean or Int: 1=connected, 0=not connected).
     */
    fun analyzeTopology(nodes: List<Any>, routingMatrix: Array<IntArray>): HomologyResult {
        val n = nodes.size
        if (n == 0) return emptyAnalysis()

        // H0: Connected components
        val visited = BooleanArray(n) { false }
        var components = 0
        for (i in 0 until n) {
            if (!visited[i]) {
                dfs(i, routingMatrix, visited)
                components++
            }
        }

        // H1: Cycles (simplified, using back-edges in DFS)
        var cycles = 0
        val visitedCycle = BooleanArray(n) { false }
        fun dfsCycle(u: Int, parent: Int): Int {
            visitedCycle[u] = true
            var localCycles = 0
            for (v in 0 until n) {
                if (routingMatrix[u][v] != 0) {
                    if (!visitedCycle[v]) {
                        localCycles += dfsCycle(v, u)
                    } else if (v != parent) {
                        localCycles++
                    }
                }
            }
            return localCycles
        }
        for (i in 0 until n) {
            if (!visitedCycle[i]) {
                cycles += dfsCycle(i, -1) / 2 // Each cycle counted twice
            }
        }

        // H2: Triangles (3-cycles)
        var triangles = 0
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (routingMatrix[i][j] != 0) {
                    for (k in j + 1 until n) {
                        if (routingMatrix[j][k] != 0 && routingMatrix[k][i] != 0) {
                            triangles++
                        }
                    }
                }
            }
        }

        val complexity = components + cycles + triangles
        val homology = HomologyResult.Homology(components, cycles, triangles)
        val metrics = HomologyResult.IntelligenceMetrics(
            informationComponents = components,
            informationCycles = cycles,
            higherOrderStructures = triangles,
            topologicalComplexity = complexity,
            networkConnectivity = HomologyResult.IntelligenceMetrics.NetworkConnectivity(connected = components <= 1)
        )
        val signature = "$components-$cycles-$triangles-$complexity"
        return HomologyResult(homology, metrics, signature)
    }

    private fun dfs(node: Int, matrix: Array<IntArray>, visited: BooleanArray) {
        visited[node] = true
        for (i in matrix[node].indices) {
            if (matrix[node][i] != 0 && !visited[i]) {
                dfs(i, matrix, visited)
            }
        }
    }

    fun emptyAnalysis(): HomologyResult {
        return HomologyResult(
            HomologyResult.Homology(0, 0, 0),
            HomologyResult.IntelligenceMetrics(
                0, 0, 0, 0,
                HomologyResult.IntelligenceMetrics.NetworkConnectivity(false)
            ),
            "0-0-0-0"
        )
    }
}
