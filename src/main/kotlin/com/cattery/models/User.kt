package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val name: String,
    val avatarUrl: String? = null,
    val role: UserRole,
)
