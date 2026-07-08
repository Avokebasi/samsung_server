package com.cattery.services

import com.cattery.database.dao.CatFemaleDao
import com.cattery.database.dao.CatMaleDao
import com.cattery.database.dao.LitterDao
import com.cattery.models.Litter
import com.cattery.models.UserRole
import com.cattery.requests.SaveLitterRequest
import com.cattery.util.LitterNames
import java.time.LocalDate

class LitterService(
    private val litterDao: LitterDao,
    private val catFemaleDao: CatFemaleDao,
    private val catMaleDao: CatMaleDao,
) {
    fun list(userId: Int, role: UserRole): List<Litter> =
        litterDao.findAll(AccessScope.ownerFilter(userId, role))

    fun search(userId: Int, role: UserRole, query: String): List<Litter> =
        litterDao.search(query, AccessScope.ownerFilter(userId, role))

    fun getById(id: Int): Litter =
        litterDao.findById(id) ?: throw IllegalArgumentException("Помёт не найден")

    fun findByMotherId(motherId: Int): List<Litter> =
        litterDao.findByMotherId(motherId)

    fun findByFatherId(fatherId: Int): List<Litter> =
        litterDao.findByFatherId(fatherId)

    fun create(ownerId: Int, request: SaveLitterRequest): Litter {
        val name = LitterNames.normalize(request.name)
        LitterNames.validate(name)
        validateParents(ownerId, request.motherId?.toInt(), request.fatherId?.toInt())
        return litterDao.create(
            ownerId = ownerId,
            name = name,
            birthDate = LocalDate.parse(request.birthDate),
            totalCount = request.totalCount.coerceAtLeast(0),
            maleCount = request.maleCount.coerceAtLeast(0),
            femaleCount = request.femaleCount.coerceAtLeast(0),
            motherId = request.motherId?.toInt(),
            fatherId = request.fatherId?.toInt(),
            photoUrls = request.photoUrls,
        )
    }

    fun update(ownerId: Int, id: Int, request: SaveLitterRequest): Litter {
        val name = LitterNames.normalize(request.name)
        LitterNames.validate(name)
        validateParents(ownerId, request.motherId?.toInt(), request.fatherId?.toInt())
        return litterDao.update(
            id = id,
            ownerId = ownerId,
            name = name,
            birthDate = LocalDate.parse(request.birthDate),
            totalCount = request.totalCount.coerceAtLeast(0),
            maleCount = request.maleCount.coerceAtLeast(0),
            femaleCount = request.femaleCount.coerceAtLeast(0),
            motherId = request.motherId?.toInt(),
            fatherId = request.fatherId?.toInt(),
            photoUrls = request.photoUrls,
        ) ?: throw IllegalArgumentException("Помёт не найден")
    }

    fun delete(ownerId: Int, id: Int): Boolean {
        if (!litterDao.delete(id, ownerId)) {
            throw IllegalArgumentException("Помёт не найден")
        }
        return true
    }

    private fun validateParents(ownerId: Int, motherId: Int?, fatherId: Int?) {
        if (motherId != null && !catFemaleDao.belongsToOwner(motherId, ownerId)) {
            throw IllegalArgumentException("Кошка не принадлежит заводчику")
        }
        if (fatherId != null && !catMaleDao.belongsToOwner(fatherId, ownerId)) {
            throw IllegalArgumentException("Кот не принадлежит заводчику")
        }
    }
}
