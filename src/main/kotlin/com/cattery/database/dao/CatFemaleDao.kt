package com.cattery.database.dao

import com.cattery.database.tables.CatFemalesTable
import com.cattery.models.CatFemale
import com.cattery.util.LikePattern
import com.cattery.util.PhotoUrlsJson
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate

class CatFemaleDao {
    fun findAll(ownerId: Int?): List<CatFemale> = transaction {
        val query = if (ownerId != null) {
            CatFemalesTable.selectAll().where { CatFemalesTable.ownerId eq ownerId }
        } else {
            CatFemalesTable.selectAll()
        }
        query.orderBy(CatFemalesTable.name to SortOrder.ASC).map { it.toCatFemale() }
    }

    fun findById(id: Int): CatFemale? = transaction {
        CatFemalesTable.selectAll()
            .where { CatFemalesTable.id eq id }
            .map { it.toCatFemale() }
            .singleOrNull()
    }

    fun search(query: String, ownerId: Int?): List<CatFemale> = transaction {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@transaction emptyList()
        val pattern = LikePattern.containsPattern(trimmed)

        CatFemalesTable.selectAll()
            .where {
                val nameFilter = CatFemalesTable.name.lowerCase() like pattern
                if (ownerId != null) {
                    nameFilter and (CatFemalesTable.ownerId eq ownerId)
                } else {
                    nameFilter
                }
            }
            .orderBy(CatFemalesTable.name to SortOrder.ASC)
            .map { it.toCatFemale() }
    }

    fun create(
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        color: String,
        matingDate: LocalDate?,
        birthDueDate: LocalDate?,
        photoUrls: List<String>,
    ): CatFemale = transaction {
        val id = CatFemalesTable.insert {
            it[CatFemalesTable.ownerId] = ownerId
            it[CatFemalesTable.name] = name
            it[CatFemalesTable.birthDate] = birthDate
            it[CatFemalesTable.color] = color
            it[CatFemalesTable.matingDate] = matingDate
            it[CatFemalesTable.birthDueDate] = birthDueDate
            it[CatFemalesTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
            it[createdAt] = Instant.now()
        } get CatFemalesTable.id
        findById(id)!!
    }

    fun update(
        id: Int,
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        color: String,
        matingDate: LocalDate?,
        birthDueDate: LocalDate?,
        photoUrls: List<String>,
    ): CatFemale? = transaction {
        val updated = CatFemalesTable.update({ (CatFemalesTable.id eq id) and (CatFemalesTable.ownerId eq ownerId) }) {
            it[CatFemalesTable.name] = name
            it[CatFemalesTable.birthDate] = birthDate
            it[CatFemalesTable.color] = color
            it[CatFemalesTable.matingDate] = matingDate
            it[CatFemalesTable.birthDueDate] = birthDueDate
            it[CatFemalesTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
        }
        if (updated == 0) null else findById(id)
    }

    fun delete(id: Int, ownerId: Int): Boolean = transaction {
        CatFemalesTable.deleteWhere { (CatFemalesTable.id eq id) and (CatFemalesTable.ownerId eq ownerId) } > 0
    }

    fun belongsToOwner(id: Int, ownerId: Int): Boolean = transaction {
        CatFemalesTable.selectAll()
            .where { (CatFemalesTable.id eq id) and (CatFemalesTable.ownerId eq ownerId) }
            .any()
    }

    private fun ResultRow.toCatFemale() = CatFemale(
        id = this[CatFemalesTable.id].toLong(),
        ownerId = this[CatFemalesTable.ownerId].toLong(),
        name = this[CatFemalesTable.name],
        birthDate = this[CatFemalesTable.birthDate].toString(),
        color = this[CatFemalesTable.color],
        matingDate = this[CatFemalesTable.matingDate]?.toString(),
        birthDueDate = this[CatFemalesTable.birthDueDate]?.toString(),
        photoUrls = PhotoUrlsJson.decode(this[CatFemalesTable.photoUrls]),
    )
}
