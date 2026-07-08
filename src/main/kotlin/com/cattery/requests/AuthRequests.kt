package com.cattery.requests

import com.cattery.models.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
    val role: UserRole,
)
