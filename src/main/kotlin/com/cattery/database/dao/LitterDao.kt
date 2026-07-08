package com.cattery.database.dao

import com.cattery.database.tables.LittersTable
import com.cattery.models.Litter
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

class LitterDao {
    fun findAll(ownerId: Int?): List<Litter> = transaction {
        val query = if (ownerId != null) {
            LittersTable.selectAll().where { LittersTable.ownerId eq ownerId }
        } else {
            LittersTable.selectAll()
        }
        query.orderBy(LittersTable.birthDate to SortOrder.DESC).map { it.toLitter() }
    }

    fun findById(id: Int): Litter? = transaction {
        LittersTable.selectAll()
            .where { LittersTable.id eq id }
            .map { it.toLitter() }
            .singleOrNull()
    }

    fun findByMotherId(motherId: Int): List<Litter> = transaction {
        LittersTable.selectAll()
            .where { LittersTable.motherId eq motherId }
            .orderBy(LittersTable.birthDate to SortOrder.DESC)
            .map { it.toLitter() }
    }

    fun findByFatherId(fatherId: Int): List<Litter> = transaction {
        LittersTable.selectAll()
            .where { LittersTable.fatherId eq fatherId }
            .orderBy(LittersTable.birthDate to SortOrder.DESC)
            .map { it.toLitter() }
    }

    fun search(query: String, ownerId: Int?): List<Litter> = transaction {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@transaction emptyList()
        val pattern = LikePattern.containsPattern(trimmed)

        LittersTable.selectAll()
            .where {
                val nameFilter = LittersTable.name.lowerCase() like pattern
                if (ownerId != null) {
                    nameFilter and (LittersTable.ownerId eq ownerId)
                } else {
                    nameFilter
                }
            }
            .orderBy(LittersTable.name to SortOrder.ASC)
            .map { it.toLitter() }
    }

    fun create(
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        totalCount: Int,
        maleCount: Int,
        femaleCount: Int,
        motherId: Int?,
        fatherId: Int?,
        photoUrls: List<String>,
    ): Litter = transaction {
        val id = LittersTable.insert {
            it[LittersTable.ownerId] = ownerId
            it[LittersTable.name] = name
            it[LittersTable.birthDate] = birthDate
            it[LittersTable.totalCount] = totalCount
            it[LittersTable.maleCount] = maleCount
            it[LittersTable.femaleCount] = femaleCount
            it[LittersTable.motherId] = motherId
            it[LittersTable.fatherId] = fatherId
            it[LittersTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
            it[createdAt] = Instant.now()
        } get LittersTable.id
        findById(id)!!
    }

    fun update(
        id: Int,
        ownerId: Int,
        name: String,
        birthDate: LocalDate,
        totalCount: Int,
        maleCount: Int,
        femaleCount: Int,
        motherId: Int?,
        fatherId: Int?,
        photoUrls: List<String>,
    ): Litter? = transaction {
        val updated = LittersTable.update({ (LittersTable.id eq id) and (LittersTable.ownerId eq ownerId) }) {
            it[LittersTable.name] = name
            it[LittersTable.birthDate] = birthDate
            it[LittersTable.totalCount] = totalCount
            it[LittersTable.maleCount] = maleCount
            it[LittersTable.femaleCount] = femaleCount
            it[LittersTable.motherId] = motherId
            it[LittersTable.fatherId] = fatherId
            it[LittersTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
        }
        if (updated == 0) null else findById(id)
    }

    fun delete(id: Int, ownerId: Int): Boolean = transaction {
        LittersTable.deleteWhere { (LittersTable.id eq id) and (LittersTable.ownerId eq ownerId) } > 0
    }

    fun belongsToOwner(id: Int, ownerId: Int): Boolean = transaction {
        LittersTable.selectAll()
            .where { (LittersTable.id eq id) and (LittersTable.ownerId eq ownerId) }
            .any()
    }

    private fun ResultRow.toLitter() = Litter(
        id = this[LittersTable.id].toLong(),
        ownerId = this[LittersTable.ownerId].toLong(),
        name = this[LittersTable.name],
        birthDate = this[LittersTable.birthDate].toString(),
        totalCount = this[LittersTable.totalCount],
        maleCount = this[LittersTable.maleCount],
        femaleCount = this[LittersTable.femaleCount],
        motherId = this[LittersTable.motherId]?.toLong(),
        fatherId = this[LittersTable.fatherId]?.toLong(),
        photoUrls = PhotoUrlsJson.decode(this[LittersTable.photoUrls]),
    )
}
