package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
data class CatMale(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val color: String = "",
    val photoUrls: List<String> = emptyList(),
)
