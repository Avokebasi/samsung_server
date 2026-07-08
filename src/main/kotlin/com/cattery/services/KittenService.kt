package com.cattery.services

import com.cattery.database.dao.CatFemaleDao
import com.cattery.database.dao.CatMaleDao
import com.cattery.database.dao.KittenDao
import com.cattery.database.dao.LitterDao
import com.cattery.models.Kitten
import com.cattery.models.KittenStatus
import com.cattery.models.UserRole
import com.cattery.requests.SaveKittenRequest
import com.cattery.responses.KittenDetailResponse
import java.math.BigDecimal
import java.time.LocalDate

class KittenService(
    private val kittenDao: KittenDao,
    private val litterDao: LitterDao,
    private val catFemaleDao: CatFemaleDao,
    private val catMaleDao: CatMaleDao,
) {
    fun listByLitter(litterId: Int): List<Kitten> {
        getLitterOrThrow(litterId)
        return kittenDao.findByLitterId(litterId)
    }

    fun search(userId: Int, role: UserRole, query: String): List<Kitten> =
        kittenDao.search(query, AccessScope.ownerFilter(userId, role))

    fun getDetail(id: Int): KittenDetailResponse {
        val kitten = kittenDao.findById(id) ?: throw IllegalArgumentException("Котёнок не найден")
        val litter = litterDao.findById(kitten.litterId.toInt())
            ?: throw IllegalArgumentException("Помёт не найден")
        val motherName = litter.motherId?.toInt()?.let { catFemaleDao.findById(it)?.name }
        val fatherName = litter.fatherId?.toInt()?.let { catMaleDao.findById(it)?.name }
        return KittenDetailResponse(
            kitten = kitten,
            litterName = litter.name,
            motherName = motherName,
            fatherName = fatherName,
        )
    }

    fun create(ownerId: Int, request: SaveKittenRequest): Kitten {
        val litterId = request.litterId.toInt()
        ensureLitterOwned(litterId, ownerId)
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        return kittenDao.create(
            litterId = litterId,
            name = name,
            birthDate = LocalDate.parse(request.birthDate),
            color = request.color.trim(),
            birthWeight = request.birthWeight?.let { BigDecimal.valueOf(it) },
            status = request.status,
            photoUrls = request.photoUrls,
        )
    }

    fun update(ownerId: Int, id: Int, request: SaveKittenRequest): Kitten {
        val existing = kittenDao.findById(id) ?: throw IllegalArgumentException("Котёнок не найден")
        val litterId = request.litterId.toInt()
        ensureLitterOwned(litterId, ownerId)
        ensureLitterOwned(existing.litterId.toInt(), ownerId)
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        return kittenDao.update(
            id = id,
            litterId = litterId,
            name = name,
            birthDate = LocalDate.parse(request.birthDate),
            color = request.color.trim(),
            birthWeight = request.birthWeight?.let { BigDecimal.valueOf(it) },
            status = request.status,
            photoUrls = request.photoUrls,
        ) ?: throw IllegalArgumentException("Котёнок не найден")
    }

    fun delete(ownerId: Int, id: Int): Boolean {
        val kitten = kittenDao.findById(id) ?: throw IllegalArgumentException("Котёнок не найден")
        ensureLitterOwned(kitten.litterId.toInt(), ownerId)
        if (!kittenDao.delete(id)) {
            throw IllegalArgumentException("Котёнок не найден")
        }
        return true
    }

    private fun getLitterOrThrow(litterId: Int) {
        if (litterDao.findById(litterId) == null) {
            throw IllegalArgumentException("Помёт не найден")
        }
    }

    private fun ensureLitterOwned(litterId: Int, ownerId: Int) {
        if (!litterDao.belongsToOwner(litterId, ownerId)) {
            throw IllegalArgumentException("Помёт не принадлежит заводчику")
        }
    }
}
