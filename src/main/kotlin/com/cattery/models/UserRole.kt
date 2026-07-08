package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    BREEDER,
    BUYER,
}
