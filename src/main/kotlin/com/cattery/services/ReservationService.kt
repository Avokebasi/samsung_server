package com.cattery.services

import com.cattery.database.dao.KittenDao
import com.cattery.database.dao.ReservationDao
import com.cattery.models.KittenStatus
import com.cattery.models.Reservation
import com.cattery.models.UserRole
import com.cattery.responses.ReservationDetailResponse

class ReservationService(
    private val reservationDao: ReservationDao,
    private val kittenDao: KittenDao,
) {
    fun list(userId: Int, role: UserRole): List<ReservationDetailResponse> =
        when (role) {
            UserRole.BREEDER -> reservationDao.findForBreeder(userId)
            UserRole.BUYER -> reservationDao.findForBuyer(userId)
        }

    fun reserve(buyerId: Int, kittenId: Int): Reservation {
        val kitten = kittenDao.findById(kittenId)
            ?: throw IllegalArgumentException("Котёнок не найден")
        if (kitten.status != KittenStatus.FREE) {
            throw IllegalArgumentException("Котёнок уже забронирован")
        }
        if (reservationDao.findActiveByKittenId(kittenId) != null) {
            throw IllegalArgumentException("Котёнок уже забронирован")
        }
        val reservation = reservationDao.create(kittenId, buyerId)
        kittenDao.updateStatus(kittenId, KittenStatus.RESERVED)
        return reservation
    }

    fun cancel(buyerId: Int, kittenId: Int): Boolean {
        val reservation = reservationDao.findActiveByKittenId(kittenId)
            ?: throw IllegalArgumentException("Активная бронь не найдена")
        if (reservation.buyerId.toInt() != buyerId) {
            throw IllegalArgumentException("Бронь принадлежит другому пользователю")
        }
        reservationDao.cancelActiveByKitten(kittenId)
        kittenDao.updateStatus(kittenId, KittenStatus.FREE)
        return true
    }
}
