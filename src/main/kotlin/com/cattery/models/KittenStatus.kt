package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
enum class KittenStatus {
    FREE,
    RESERVED,
}
