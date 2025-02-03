package com.example.learningandroid.models

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
