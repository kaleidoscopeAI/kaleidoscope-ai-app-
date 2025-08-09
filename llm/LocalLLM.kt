package com.example.kaleidoscopeai.llm

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

/**
 * LocalLLM: Robust LLM integration with graceful fallback when Ollama server is unavailable.
 * Supports both local Ollama server and fallback to mock responses.
 */
class LocalLLM(
    private val modelPath: String = "llama3.2",
    private val ollamaUrl: String = "http://localhost:11434",
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.UNKNOWN)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus
    
    private val _isHealthy = MutableStateFlow(false)
    val isHealthy: StateFlow<Boolean> = _isHealthy
    
    enum class ConnectionStatus {
        UNKNOWN, CONNECTED, DISCONNECTED, ERROR
    }
    
    data class LLMResponse(
        val content: String,
        val isFromFallback: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    init {
        // Start health check coroutine
        startHealthMonitoring()
    }
    
    private fun startHealthMonitoring() {
        coroutineScope.launch {
            while (isActive) {
                try {
                    val isConnected = checkOllamaHealth()
                    _isHealthy.value = isConnected
                    _connectionStatus.value = if (isConnected) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
                    delay(30000) // Check every 30 seconds
                } catch (e: Exception) {
                    _isHealthy.value = false
                    _connectionStatus.value = ConnectionStatus.ERROR
                    delay(10000) // Retry sooner on error
                }
            }
        }
    }
    
    private suspend fun checkOllamaHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$ollamaUrl/api/tags")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val responseCode = connection.responseCode
            connection.disconnect()
            
            responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun generate(prompt: String, maxTokens: Int = 128): LLMResponse {
        return if (_isHealthy.value) {
            generateWithOllama(prompt, maxTokens)
        } else {
            generateFallback(prompt, maxTokens)
        }
    }
    
    private suspend fun generateWithOllama(prompt: String, maxTokens: Int): LLMResponse = withContext(Dispatchers.IO) {
        try {
            val url = URL("$ollamaUrl/api/generate")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 30000
            
            val requestBody = JSONObject().apply {
                put("model", modelPath)
                put("prompt", prompt)
                put("stream", false)
                put("options", JSONObject().apply {
                    put("num_predict", maxTokens)
                    put("temperature", 0.7)
                })
            }
            
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    val response = reader.readText()
                    val jsonResponse = JSONObject(response)
                    val content = jsonResponse.optString("response", "")
                    
                    connection.disconnect()
                    LLMResponse(content, false)
                }
            } else {
                connection.disconnect()
                _isHealthy.value = false
                _connectionStatus.value = ConnectionStatus.ERROR
                generateFallback(prompt, maxTokens)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _isHealthy.value = false
            _connectionStatus.value = ConnectionStatus.ERROR
            generateFallback(prompt, maxTokens)
        }
    }
    
    private fun generateFallback(prompt: String, maxTokens: Int): LLMResponse {
        // Intelligent fallback responses based on prompt analysis
        val response = when {
            prompt.contains("reflect", ignoreCase = true) || prompt.contains("think", ignoreCase = true) -> {
                "I am processing this information and integrating it into my understanding. The patterns suggest interesting connections forming."
            }
            prompt.contains("analyze", ignoreCase = true) || prompt.contains("topology", ignoreCase = true) -> {
                "The current topology shows ${(Math.random() * 100).toInt()}% connectivity with emerging clusters. Stress patterns indicate adaptation."
            }
            prompt.contains("energy", ignoreCase = true) || prompt.contains("state", ignoreCase = true) -> {
                "Energy levels are fluctuating within normal parameters. System coherence is maintained at ${(60 + Math.random() * 40).toInt()}%."
            }
            prompt.contains("learn", ignoreCase = true) || prompt.contains("knowledge", ignoreCase = true) -> {
                "Learning processes active. Knowledge integration proceeding through distributed node network."
            }
            else -> {
                "Processing input through distributed cognitive architecture. Patterns emerging across ${(Math.random() * 125).toInt()} active nodes."
            }
        }
        
        return LLMResponse(response, true)
    }
    
    fun shutdown() {
        coroutineScope.cancel()
    }
}