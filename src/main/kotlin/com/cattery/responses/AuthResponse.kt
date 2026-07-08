package com.cattery.responses

import com.cattery.models.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: User,
)

@Serializable
data class ErrorResponse(
    val message: String,
    val code: String? = null,
)
