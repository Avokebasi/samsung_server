package com.cattery.requests

import com.cattery.models.KittenStatus
import kotlinx.serialization.Serializable

@Serializable
data class SaveCatFemaleRequest(
    val name: String,
    val birthDate: String,
    val matingDate: String? = null,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveCatMaleRequest(
    val name: String,
    val birthDate: String,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveLitterRequest(
    val name: String,
    val birthDate: String,
    val totalCount: Int,
    val maleCount: Int,
    val femaleCount: Int,
    val motherId: Long? = null,
    val fatherId: Long? = null,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveKittenRequest(
    val litterId: Long,
    val name: String,
    val birthDate: String,
    val color: String,
    val birthWeight: Double? = null,
    val status: KittenStatus = KittenStatus.FREE,
    val photoUrls: List<String> = emptyList(),
)
