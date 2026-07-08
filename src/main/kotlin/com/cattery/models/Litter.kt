package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
data class Litter(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val totalCount: Int,
    val maleCount: Int,
    val femaleCount: Int,
    val motherId: Long? = null,
    val fatherId: Long? = null,
    val photoUrls: List<String> = emptyList(),
)
