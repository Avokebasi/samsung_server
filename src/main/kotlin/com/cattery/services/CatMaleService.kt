package com.cattery.services

import com.cattery.database.dao.CatMaleDao
import com.cattery.models.CatMale
import com.cattery.models.UserRole
import com.cattery.requests.SaveCatMaleRequest
import java.time.LocalDate

class CatMaleService(
    private val catMaleDao: CatMaleDao,
) {
    fun list(userId: Int, role: UserRole): List<CatMale> =
        catMaleDao.findAll(AccessScope.ownerFilter(userId, role))

    fun search(userId: Int, role: UserRole, query: String): List<CatMale> =
        catMaleDao.search(query, AccessScope.ownerFilter(userId, role))

    fun getById(id: Int): CatMale =
        catMaleDao.findById(id) ?: throw IllegalArgumentException("Кот не найден")

    fun create(ownerId: Int, request: SaveCatMaleRequest): CatMale {
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        val color = request.color.trim()
        if (color.isBlank()) throw IllegalArgumentException("Укажите окрас")
        val birthDate = LocalDate.parse(request.birthDate)
        return catMaleDao.create(
            ownerId = ownerId,
            name = name,
            birthDate = birthDate,
            color = color,
            photoUrls = request.photoUrls,
        )
    }

    fun update(ownerId: Int, id: Int, request: SaveCatMaleRequest): CatMale {
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        val color = request.color.trim()
        if (color.isBlank()) throw IllegalArgumentException("Укажите окрас")
        val birthDate = LocalDate.parse(request.birthDate)
        return catMaleDao.update(
            id = id,
            ownerId = ownerId,
            name = name,
            birthDate = birthDate,
            color = color,
            photoUrls = request.photoUrls,
        ) ?: throw IllegalArgumentException("Кот не найден")
    }

    fun delete(ownerId: Int, id: Int): Boolean {
        if (!catMaleDao.delete(id, ownerId)) {
            throw IllegalArgumentException("Кот не найден")
        }
        return true
    }
}
