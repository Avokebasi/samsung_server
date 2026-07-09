package com.cattery.services

import com.cattery.database.dao.CatFemaleDao
import com.cattery.models.CatFemale
import com.cattery.models.UserRole
import com.cattery.requests.SaveCatFemaleRequest
import com.cattery.util.MatingDates
import java.time.LocalDate

class CatFemaleService(
    private val catFemaleDao: CatFemaleDao,
) {
    fun list(userId: Int, role: UserRole): List<CatFemale> =
        catFemaleDao.findAll(AccessScope.ownerFilter(userId, role))

    fun search(userId: Int, role: UserRole, query: String): List<CatFemale> =
        catFemaleDao.search(query, AccessScope.ownerFilter(userId, role))

    fun getById(id: Int): CatFemale =
        catFemaleDao.findById(id) ?: throw IllegalArgumentException("Кошка не найдена")

    fun create(ownerId: Int, request: SaveCatFemaleRequest): CatFemale {
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        val color = request.color.trim()
        if (color.isBlank()) throw IllegalArgumentException("Укажите окрас")
        val birthDate = LocalDate.parse(request.birthDate)
        val matingDate = request.matingDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val birthDueDate = MatingDates.birthDueDate(matingDate)
        return catFemaleDao.create(
            ownerId = ownerId,
            name = name,
            birthDate = birthDate,
            color = color,
            matingDate = matingDate,
            birthDueDate = birthDueDate,
            photoUrls = request.photoUrls,
        )
    }

    fun update(ownerId: Int, id: Int, request: SaveCatFemaleRequest): CatFemale {
        val name = request.name.trim()
        if (name.isBlank()) throw IllegalArgumentException("Укажите кличку")
        val color = request.color.trim()
        if (color.isBlank()) throw IllegalArgumentException("Укажите окрас")
        val birthDate = LocalDate.parse(request.birthDate)
        val matingDate = request.matingDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val birthDueDate = MatingDates.birthDueDate(matingDate)
        return catFemaleDao.update(
            id = id,
            ownerId = ownerId,
            name = name,
            birthDate = birthDate,
            color = color,
            matingDate = matingDate,
            birthDueDate = birthDueDate,
            photoUrls = request.photoUrls,
        ) ?: throw IllegalArgumentException("Кошка не найдена")
    }

    fun delete(ownerId: Int, id: Int): Boolean {
        if (!catFemaleDao.delete(id, ownerId)) {
            throw IllegalArgumentException("Кошка не найдена")
        }
        return true
    }
}
