package com.example.learningandroid.models

data class FeedbackModel(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val content: String = "",
    var imageUrl: String = "",
    val userId: String = "",
    val status: String = ""
)
