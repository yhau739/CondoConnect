package com.example.learningandroid.models

data class FetchBill(
    val month: String = "",
    val year: Int = 0,
    val amount: Double = 0.0,
    val status: String = "",
    val unitId: String = ""
)
