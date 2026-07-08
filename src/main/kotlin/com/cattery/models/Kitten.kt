package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
data class Kitten(
    val id: Long,
    val litterId: Long,
    val name: String,
    val birthDate: String,
    val color: String,
    val birthWeight: Double? = null,
    val status: KittenStatus,
    val photoUrls: List<String> = emptyList(),
)
