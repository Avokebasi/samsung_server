package com.cattery.database.dao

import com.cattery.database.tables.KittensTable
import com.cattery.database.tables.LittersTable
import com.cattery.models.Kitten
import com.cattery.models.KittenStatus
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
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class KittenDao {
    fun findByLitterId(litterId: Int): List<Kitten> = transaction {
        KittensTable.selectAll()
            .where { KittensTable.litterId eq litterId }
            .orderBy(KittensTable.name to SortOrder.ASC)
            .map { it.toKitten() }
    }

    fun findById(id: Int): Kitten? = transaction {
        KittensTable.selectAll()
            .where { KittensTable.id eq id }
            .map { it.toKitten() }
            .singleOrNull()
    }

    fun search(query: String, ownerId: Int?): List<Kitten> = transaction {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@transaction emptyList()
        val pattern = LikePattern.containsPattern(trimmed)

        val litterIds = if (ownerId != null) {
            LittersTable.selectAll()
                .where { LittersTable.ownerId eq ownerId }
                .map { it[LittersTable.id] }
        } else {
            null
        }

        if (litterIds != null && litterIds.isEmpty()) return@transaction emptyList()

        KittensTable.selectAll()
            .where {
                val nameFilter = KittensTable.name.lowerCase() like pattern
                if (litterIds != null) {
                    nameFilter and (KittensTable.litterId inList litterIds)
                } else {
                    nameFilter
                }
            }
            .orderBy(KittensTable.name to SortOrder.ASC)
            .map { it.toKitten() }
    }

    fun create(
        litterId: Int,
        name: String,
        birthDate: LocalDate,
        color: String,
        birthWeight: BigDecimal?,
        status: KittenStatus,
        photoUrls: List<String>,
    ): Kitten = transaction {
        val id = KittensTable.insert {
            it[KittensTable.litterId] = litterId
            it[KittensTable.name] = name
            it[KittensTable.birthDate] = birthDate
            it[KittensTable.color] = color
            it[KittensTable.birthWeight] = birthWeight
            it[KittensTable.status] = status.name
            it[KittensTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
            it[createdAt] = Instant.now()
        } get KittensTable.id
        findById(id)!!
    }

    fun update(
        id: Int,
        litterId: Int,
        name: String,
        birthDate: LocalDate,
        color: String,
        birthWeight: BigDecimal?,
        status: KittenStatus,
        photoUrls: List<String>,
    ): Kitten? = transaction {
        val updated = KittensTable.update({ KittensTable.id eq id }) {
            it[KittensTable.litterId] = litterId
            it[KittensTable.name] = name
            it[KittensTable.birthDate] = birthDate
            it[KittensTable.color] = color
            it[KittensTable.birthWeight] = birthWeight
            it[KittensTable.status] = status.name
            it[KittensTable.photoUrls] = PhotoUrlsJson.encode(photoUrls)
        }
        if (updated == 0) null else findById(id)
    }

    fun updateStatus(id: Int, status: KittenStatus): Boolean = transaction {
        KittensTable.update({ KittensTable.id eq id }) {
            it[KittensTable.status] = status.name
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        KittensTable.deleteWhere { KittensTable.id eq id } > 0
    }

    private fun ResultRow.toKitten() = Kitten(
        id = this[KittensTable.id].toLong(),
        litterId = this[KittensTable.litterId].toLong(),
        name = this[KittensTable.name],
        birthDate = this[KittensTable.birthDate].toString(),
        color = this[KittensTable.color],
        birthWeight = this[KittensTable.birthWeight]?.toDouble(),
        status = KittenStatus.valueOf(this[KittensTable.status]),
        photoUrls = PhotoUrlsJson.decode(this[KittensTable.photoUrls]),
    )
}
