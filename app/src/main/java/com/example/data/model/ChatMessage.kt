package com.example.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isCreatorResponse: Boolean = false,
    val isError: Boolean = false,
    val isThinking: Boolean = false
)
