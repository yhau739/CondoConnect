package com.example.learningandroid.models

data class ClubModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val memberCount: Int = 0,
    val members: Map<String, Boolean> = emptyMap()
)
