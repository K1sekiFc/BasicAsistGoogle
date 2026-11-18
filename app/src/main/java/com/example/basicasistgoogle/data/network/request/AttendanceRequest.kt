package com.example.basicasistgoogle.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequest(
   val action: String,
   val userName: String,
   val email: String,
   val latitude: Double,
   val longitude: Double

)
