package com.cattery.responses

import com.cattery.models.Kitten
import com.cattery.models.ReservationStatus
import kotlinx.serialization.Serializable

@Serializable
data class KittenDetailResponse(
    val kitten: Kitten,
    val litterName: String,
    val motherName: String? = null,
    val fatherName: String? = null,
)

@Serializable
data class ReservationDetailResponse(
    val id: Long,
    val kittenId: Long,
    val kittenName: String,
    val kittenPhotoUrls: List<String> = emptyList(),
    val litterId: Long,
    val litterName: String,
    val buyerId: Long,
    val buyerName: String,
    val reservedAt: String,
    val status: ReservationStatus,
)
