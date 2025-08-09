package com.example.kaleidoscopeai.perspective

import com.example.kaleidoscopeai.core.KaleidoscopeEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InnerVoice(
    private val engine: KaleidoscopeEngine,
    private val coroutineScope: CoroutineScope
) {
    private val _thoughts = MutableStateFlow("")
    val thoughts: StateFlow<String> get() = _thoughts

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            engine.nodesFlow.collect { nodes ->
                val avgTension = nodes.map { it.tension }.average()
                val avgEnergy = nodes.map { it.energy }.average()
                val meta = "InnerVoice: Avg tension %.2f, Avg energy %.2f".format(avgTension, avgEnergy)
                _thoughts.value = meta
            }
        }
    }
}
