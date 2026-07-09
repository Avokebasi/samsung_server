package com.cattery.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarRequest(
    val avatarUrl: String?,
)
