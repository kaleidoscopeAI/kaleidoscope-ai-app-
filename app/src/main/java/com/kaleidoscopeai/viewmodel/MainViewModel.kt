
package com.kaleidoscopeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.kaleidoscopeai.core.KaleidoscopeEngine
import com.example.kaleidoscopeai.core.Node
import com.example.kaleidoscopeai.core.Bond

class MainViewModel : ViewModel() {
    // Core engine instance
    private val engine = KaleidoscopeEngine(coroutineScope = viewModelScope)

    // Expose state flows for UI
    val nodes: StateFlow<List<Node>> = engine.nodesFlow
    val bonds: StateFlow<List<Bond>> = engine.bondsFlow
    val homologySignature: StateFlow<String?> get() = _homologySignature.asStateFlow()
    private val _homologySignature = MutableStateFlow<String?>(null)

    init {
        // Observe engine metrics
        viewModelScope.launch {
            engine.nodesFlow.collect { /* trigger recomposition if needed */ }
        }
        viewModelScope.launch {
            engine.bondsFlow.collect { /* trigger recomposition if needed */ }
        }
        // Optionally, update metrics
        viewModelScope.launch {
            while (true) {
                engine.globalStateUpdate()
                _homologySignature.value = engine.lastHomologySignature
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    // Example: external input to engine
    fun updateWithInput(input: Map<String, Any>) {
        engine.globalStateUpdate(externalInput = input)
    }
}
