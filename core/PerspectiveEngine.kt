package com.example.kaleidoscopeai.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.example.kaleidoscopeai.llm.OllamaClient

class PerspectiveEngine(
    private val kaleidoscopeEngine: KaleidoscopeEngine,
    private val coroutineScope: CoroutineScope,
    private val ollamaClient: OllamaClient
) {
    private var listenJob: Job? = null

    fun start() {
        if (listenJob?.isActive == true) return
        listenJob = coroutineScope.launch(Dispatchers.IO) {
            kaleidoscopeEngine.nodesFlow.collect { nodes ->
                val reflections = nodes.mapNotNull { it.reflection }.joinToString("\n")
                if (reflections.isNotBlank()) {
                    val response = ollamaClient.sendPrompt(reflections)
                    parseAndInjectFeedback(response)
                }
            }
        }
    }

    fun stop() { listenJob?.cancel() }

    private fun parseAndInjectFeedback(response: String) {
        val lines = response.lines()
        val regex = Regex("Node (\\d+): (.+)")
        for (line in lines) {
            val match = regex.matchEntire(line.trim()) ?: continue
            val (nodeId, feedback) = match.destructured
            kaleidoscopeEngine.injectReflection(nodeId.toInt(), feedback)
        }
    }
}
