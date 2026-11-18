package com.example.basicasistgoogle.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.basicasistgoogle.data.local.SessionManager
import com.example.basicasistgoogle.data.network.api.AttendanceApiService
import com.example.basicasistgoogle.data.network.request.AttendanceRequest
import com.example.basicasistgoogle.data.network.request.AuthorizeUserRequest
import com.example.basicasistgoogle.data.network.response.AttendanceResponse
import com.example.basicasistgoogle.domain.entity.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.basicasistgoogle.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
     private val apiService: AttendanceApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun loginWithGoogle(activityContext: Context): Result<UserData> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val webClientId = "402542134538-41nohdg4kvosd3h4jj6pijo5v481g76p.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .setPreferImmediatelyAvailableCredentials(false)
                .build()

            val result = credentialManager.getCredential(context = activityContext, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // 1. Extraemos los datos del usuario de Google
                val userData = UserData(
                    email = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName,
                    profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString()
                )

                val accessResponse = apiService.authorizeUser(
                    AuthorizeUserRequest(
                        action = "authorize",
                        email = userData.email,
                        userName = userData.displayName?.takeIf { it.isNotBlank() } ?: userData.email
                    )
                )
                val accessBody = accessResponse.body()
                if (!accessResponse.isSuccessful || accessBody?.status != "authorized") {
                    return Result.failure(
                        Exception(accessBody?.message ?: "No se pudo comprobar el acceso a la app.")
                    )
                }

                // Keep the session only after the Google account has been approved.
                sessionManager.saveUserData(userData)

                Result.success(userData)
            } else {
                Result.failure(Exception("Tipo de credencial no soportado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSessionUser(): UserData? {
        return sessionManager.getUserData()
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
    override suspend fun submitAttendance(userName: String, email: String, latitude: Double, longitude: Double): Result<Unit> {
        return withContext(Dispatchers.IO) { // Ejecución segura en segundo plano para operaciones de red
            try {

                val requestBody = AttendanceRequest(
                    action = "attendance",
                    userName = userName,
                    email = email,
                    latitude = latitude,
                    longitude = longitude
                )
                val response = apiService.registerAttendance(requestBody)

                val responseBody = response.body()
                if (response.isSuccessful && responseBody?.status == "success") {
                    Result.success(Unit)
                } else {
                    Result.failure(
                        Exception(
                            responseBody?.message
                                ?: "Error en servidor Google Sheets: HTTP ${response.code()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
