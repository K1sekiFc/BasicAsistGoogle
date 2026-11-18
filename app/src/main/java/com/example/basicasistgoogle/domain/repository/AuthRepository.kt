package com.example.basicasistgoogle.domain.repository

import android.content.Context
import com.example.basicasistgoogle.domain.entity.UserData

interface AuthRepository {
    suspend fun loginWithGoogle(activityContext: Context): Result<UserData>
    suspend fun submitAttendance(userName: String, email: String, latitude: Double, longitude: Double): Result<Unit>
    fun getSessionUser(): UserData?
    suspend fun logout()
}
