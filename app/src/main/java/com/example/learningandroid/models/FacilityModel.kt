package com.example.learningandroid

import com.example.learningandroid.models.FacilityBookingModel

data class FacilityModel(
    var id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val bookings: Map<String, FacilityBookingModel>? = null // Map to hold bookings
)
