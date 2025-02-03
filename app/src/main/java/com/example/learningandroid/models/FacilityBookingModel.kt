package com.example.learningandroid.models

data class FacilityBookingModel(
    var id: String = "",
    var uid: String ="",
    val bookingDate: String = "",
    val bookingStartTime: String = "",
    val bookingEndTime: String = "",
    var bookingStatus: String = "",
    val imageUrl: String = "",
    val title: String = ""
)
