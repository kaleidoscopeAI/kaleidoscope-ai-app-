package com.example.kaleidoscopeai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaleidoscopeai.core.KaleidoscopeEngine
import com.example.kaleidoscopeai.core.PerspectiveEngine
import com.example.kaleidoscopeai.core.SuperNode
import com.example.kaleidoscopeai.data.MemoryRepository
import com.example.kaleidoscopeai.llm.LocalLLM
import com.example.kaleidoscopeai.web.WebCrawlerEngine
import com.example.kaleidoscopeai.perspective.WebCrawlerPerspective
import com.example.kaleidoscopeai.perspective.InnerVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KaleidoscopeViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {
    val engine = KaleidoscopeEngine(nodeSpacing = 1f, coroutineScope = viewModelScope)
    val localLLM = LocalLLM(modelPath = "llama-model.bin")
    val perspectiveEngine = PerspectiveEngine(localLLM)
    val superNode = SuperNode(viewModelScope)
    val webCrawler = WebCrawlerEngine(engine, viewModelScope)
    val webCrawlerPerspective = WebCrawlerPerspective(engine, webCrawler, viewModelScope)
    val innerVoice = InnerVoice(engine, viewModelScope)

    val nodesFlow: StateFlow<List<com.example.kaleidoscopeai.core.Node>> = engine.nodesFlow
    val bondsFlow: StateFlow<List<com.example.kaleidoscopeai.core.Bond>> = engine.bondsFlow
    val webInsights = webCrawlerPerspective.webInsights
    val innerThoughts = innerVoice.thoughts

    init {
        // Start perspectives and inner voice
        webCrawlerPerspective.startCrawling(listOf(
            "https://en.wikipedia.org/wiki/Artificial_intelligence",
            "https://arxiv.org/list/cs.AI/recent"
        ))
        innerVoice.start()
    }

    fun saveNodeReflection(nodeId: Int, text: String) {
        viewModelScope.launch {
            memoryRepository.saveNodeReflection(
                com.example.kaleidoscopeai.data.NodeReflection(
                    nodeId = nodeId,
                    timestamp = System.currentTimeMillis(),
                    reflectionText = text
                )
            )
        }
    }

    fun applyExternalForceToNode(nodeId: Int, force: com.example.kaleidoscopeai.core.Vec3) {
        engine.applyForceToNode(nodeId, force)
    }

    fun enqueueUrlToCrawl(url: String) {
        webCrawler.enqueueUrl(url)
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up resources if needed
    }
}
