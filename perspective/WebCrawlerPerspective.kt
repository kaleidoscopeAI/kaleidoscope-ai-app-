package com.example.kaleidoscopeai.perspective

import com.example.kaleidoscopeai.core.KaleidoscopeEngine
import com.example.kaleidoscopeai.web.WebCrawlerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WebCrawlerPerspective(
    private val engine: KaleidoscopeEngine,
    private val webCrawler: WebCrawlerEngine,
    private val coroutineScope: CoroutineScope
) {
    private val _webInsights = MutableStateFlow("")
    val webInsights: StateFlow<String> get() = _webInsights

    fun startCrawling(urls: List<String>) {
        urls.forEach { url ->
            webCrawler.enqueueUrl(url)
        }
        coroutineScope.launch(Dispatchers.IO) {
            // Listen for new reflections injected by crawler
            engine.nodesFlow.collect { nodes ->
                val latest = nodes.mapNotNull { it.reflection }.lastOrNull()
                if (latest != null) _webInsights.value = latest
            }
        }
    }
}
