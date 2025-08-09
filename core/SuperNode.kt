package com.example.kaleidoscopeai.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import core.HomologicalAnalyzer

class SuperNode(
    private val kaleidoscopeEngine: KaleidoscopeEngine,
    private val coroutineScope: CoroutineScope
) {
    private val _superReflectionFlow = MutableStateFlow<String?>(null)
    val superReflectionFlow: StateFlow<String?> get() = _superReflectionFlow
    private var superJob: Job? = null

    fun start() {
        if (superJob?.isActive == true) return
        superJob = coroutineScope.launch(Dispatchers.Default) {
            kaleidoscopeEngine.nodesFlow.collect { nodes ->
                val avgTension = nodes.map { it.tension }.average().toFloat()
                val meta = "SuperNode: Avg tension $avgTension"
                _superReflectionFlow.emit(meta)
            }
        }
    }

    fun stop() { superJob?.cancel() }
}
