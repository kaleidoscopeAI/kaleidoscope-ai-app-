package com.example.kaleidoscope.app.network

import com.example.kaleidoscope.shared.models.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import okhttp3.*
import java.util.concurrent.TimeUnit

class KaleidoscopeWebSocket(private val serverUrl: String = "ws://localhost:8080") {
    private var metricsWebSocket: WebSocket? = null
    private var nodesWebSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val _metrics = Channel<SystemMetrics>(Channel.BUFFERED)
    val metrics: Flow<SystemMetrics> = _metrics.receiveAsFlow()

    private val _nodes = Channel<List<Node>>(Channel.BUFFERED)
    val nodes: Flow<List<Node>> = _nodes.receiveAsFlow()

    fun connect() {
        connectMetrics()
        connectNodes()
    }

    private fun connectMetrics() {
        val request = Request.Builder()
            .url("$serverUrl/metrics")
            .build()

        metricsWebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val metrics = Json.decodeFromString(SystemMetrics.serializer(), text)
                    _metrics.trySend(metrics)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                // Implement reconnection logic here
            }
        })
    }

    private fun connectNodes() {
        val request = Request.Builder()
            .url("$serverUrl/nodes")
            .build()

        nodesWebSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val nodes = Json.decodeFromString(List.serializer(Node.serializer()), text)
                    _nodes.trySend(nodes)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                // Implement reconnection logic here
            }
        })
    }

    fun disconnect() {
        metricsWebSocket?.close(1000, "Disconnecting")
        nodesWebSocket?.close(1000, "Disconnecting")
    }
}
