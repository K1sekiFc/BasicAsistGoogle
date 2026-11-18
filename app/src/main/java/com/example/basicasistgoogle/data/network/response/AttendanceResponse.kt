package com.example.basicasistgoogle.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(
    val status: String,
    val message: String? = null
)
