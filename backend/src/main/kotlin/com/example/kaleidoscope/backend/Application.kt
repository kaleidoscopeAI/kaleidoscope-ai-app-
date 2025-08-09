package com.example.kaleidoscope.backend

import com.example.kaleidoscope.shared.KaleidoscopeAI
import com.example.kaleidoscope.shared.models.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val kaleidoscope = KaleidoscopeAI()
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var simulationJob: Job? = null

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/metrics") {
            try {
                kaleidoscope.systemMetrics
                    .onEach { metrics ->
                        val json = Json.encodeToString(SystemMetrics.serializer(), metrics)
                        outgoing.send(Frame.Text(json))
                    }
                    .collect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        webSocket("/nodes") {
            try {
                while (true) {
                    val nodes = kaleidoscope.nodes.values.toList()
                    val json = Json.encodeToString(List.serializer(Node.serializer()), nodes)
                    outgoing.send(Frame.Text(json))
                    delay(16) // ~60 FPS
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        post("/simulation/start") {
            simulationJob?.cancel()
            simulationJob = scope.launch {
                while (isActive) {
                    kaleidoscope.updateSystem(0.016)
                    delay(16)
                }
            }
            call.respond(mapOf("status" to "started"))
        }

        post("/simulation/stop") {
            simulationJob?.cancel()
            simulationJob = null
            call.respond(mapOf("status" to "stopped"))
        }

        post("/nodes") {
            val node = call.receive<CreateNodeRequest>()
            val id = kaleidoscope.createNode(node.position)
            call.respond(mapOf("id" to id))
        }
    }
}

@kotlinx.serialization.Serializable
data class CreateNodeRequest(
    val position: Vector3D
)
