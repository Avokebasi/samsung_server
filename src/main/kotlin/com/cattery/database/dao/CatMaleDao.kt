package com.cattery.database.dao

import com.cattery.database.tables.CatMalesTable
import com.cattery.models.CatMale
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

class CatMaleDao {
    fun findAll(ownerId: Int?): List<CatMale> = transaction {
        val query = if (ownerId != null) {
            CatMalesTable.selectAll().where { CatMalesTable.ownerId eq ownerId }
        } else {
            CatMalesTable.selectAll()
        }
        query.orderBy(CatMalesTable.name to SortOrder.ASC).map { it.toCatMale() }
    }

    fun findById(id: Int): CatMale? = transaction {
        CatMalesTable.selectAll()
            .where { CatMalesTable.id eq id }
            .map { it.toCatMale() }
            .singleOrNull()
    }

    fun search(query: String, ownerId: Int?): List<CatMale> = transaction {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@transaction emptyList()
        val pattern = LikePattern.containsPattern(trimmed)

        CatMalesTable.selectAll()
            .where {
                val nameFilter = CatMalesTable.name.lowerCase() like pattern
                if (ownerId != null) {
                    nameFilter and (CatMalesTable.ownerId eq ownerId)
                } else {
                    nameFilter
                }
            }
            .orderBy(CatMalesTable.name to SortOrder.ASC)
            .map { it.toCatMale() }
    }

    fun create(
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        photoUrls: List<String>,
    ): CatMale = transaction {
        val id = CatMalesTable.insert {
            it[CatMalesTable.ownerId] = ownerId
            it[CatMalesTable.name] = name
            it[CatMalesTable.birthDate] = birthDate
            it[CatMalesTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
            it[createdAt] = Instant.now()
        } get CatMalesTable.id
        findById(id)!!
    }

    fun update(
        id: Int,
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        photoUrls: List<String>,
    ): CatMale? = transaction {
        val updated = CatMalesTable.update({ (CatMalesTable.id eq id) and (CatMalesTable.ownerId eq ownerId) }) {
            it[CatMalesTable.name] = name
            it[CatMalesTable.birthDate] = birthDate
            it[CatMalesTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
        }
        if (updated == 0) null else findById(id)
    }

    fun delete(id: Int, ownerId: Int): Boolean = transaction {
        CatMalesTable.deleteWhere { (CatMalesTable.id eq id) and (CatMalesTable.ownerId eq ownerId) } > 0
    }

    fun belongsToOwner(id: Int, ownerId: Int): Boolean = transaction {
        CatMalesTable.selectAll()
            .where { (CatMalesTable.id eq id) and (CatMalesTable.ownerId eq ownerId) }
            .any()
    }

    private fun ResultRow.toCatMale() = CatMale(
        id = this[CatMalesTable.id].toLong(),
        ownerId = this[CatMalesTable.ownerId].toLong(),
        name = this[CatMalesTable.name],
        birthDate = this[CatMalesTable.birthDate].toString(),
        photoUrls = PhotoUrlsJson.decode(this[CatMalesTable.photoUrls]),
    )
}
