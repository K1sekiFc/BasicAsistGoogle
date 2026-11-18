package com.example.basicasistgoogle.data.network.api

import com.example.basicasistgoogle.data.network.request.AttendanceRequest
import com.example.basicasistgoogle.data.network.request.AuthorizeUserRequest
import com.example.basicasistgoogle.data.network.response.AttendanceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AttendanceApiService {
    @POST("exec")
    suspend fun registerAttendance(
        @Body request: AttendanceRequest
    ): Response<AttendanceResponse>

    @POST("exec")
    suspend fun authorizeUser(
        @Body request: AuthorizeUserRequest
    ): Response<AttendanceResponse>
}
