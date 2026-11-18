package com.example.basicasistgoogle.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeUserRequest(
    val action: String,
    val email: String,
    val userName: String
)
