package com.example.learningandroid.models

data class VisitorModel(
    var id: String = "",
    val name: String = "",
    val unitNo: String = "",
    val registeredBy: String = "",
    val contactNo: String = "",
    val vehicleNo: String = "",
    val status: String = "",
    val validTill: String = "",
    val visitorCode: String = "",
    val checkInTime: String = "",  // Added field
    val checkOutTime: String = ""  // Added field
)
