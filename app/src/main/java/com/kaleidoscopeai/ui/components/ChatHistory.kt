package com.kaleidoscopeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatHistory(messages: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Color(0xFF181C24))
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        for (msg in messages) {
            Text(msg, color = Color.White, modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}
