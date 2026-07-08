package com.cattery.models

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationStatus {
    ACTIVE,
    CANCELLED,
}

@Serializable
data class Reservation(
    val id: Long,
    val kittenId: Long,
    val buyerId: Long,
    val reservedAt: String,
    val status: ReservationStatus,
)
