package com.kaleidoscopeai.ui.screens

import androidx.compose.runtime.Composable
import com.kaleidoscopeai.ui.components.ChatHistory
import com.kaleidoscopeai.ui.components.ChatInputBar

@Composable
fun ChatScreen(messages: List<String>, onSend: (String) -> Unit) {
    ChatHistory(messages = messages)
    ChatInputBar(onSend = onSend)
}
