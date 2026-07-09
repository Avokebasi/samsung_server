package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
data class CatFemale(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val color: String = "",
    val matingDate: String? = null,
    val birthDueDate: String? = null,
    val photoUrls: List<String> = emptyList(),
)
