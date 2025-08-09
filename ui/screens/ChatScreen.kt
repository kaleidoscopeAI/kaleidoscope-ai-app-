package com.example.kaleidoscopeai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kaleidoscopeai.llm.OllamaClient
import com.example.kaleidoscopeai.ui.viewmodel.KaleidoscopeViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(viewModel: KaleidoscopeViewModel) {
    var input by remember { mutableStateOf("") }
    var chatLog by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Chat with Kaleidoscope AI (Ollama LLM)")
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            chatLog.forEach { msg ->
                Text(msg)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth().background(Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (input.isNotBlank()) {
                isLoading = true
                scope.launch {
                    // Simulate LLM call
                    chatLog = chatLog + "You: $input"
                    chatLog = chatLog + "AI: (response from Ollama)"
                    input = ""
                    isLoading = false
                }
            }
        }) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
            else Text("Send")
        }
    }
}
