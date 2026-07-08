package com.cattery.database.dao

import com.cattery.database.tables.KittensTable
import com.cattery.database.tables.LittersTable
import com.cattery.database.tables.ReservationsTable
import com.cattery.database.tables.UsersTable
import com.cattery.models.Reservation
import com.cattery.models.ReservationStatus
import com.cattery.responses.ReservationDetailResponse
import com.cattery.util.PhotoUrlsJson
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class ReservationDao {
    fun findActiveByKittenId(kittenId: Int): Reservation? = transaction {
        ReservationsTable.selectAll()
            .where { (ReservationsTable.kittenId eq kittenId) and (ReservationsTable.status eq ReservationStatus.ACTIVE.name) }
            .map { it.toReservation() }
            .singleOrNull()
    }

    fun findActiveById(id: Int): Reservation? = transaction {
        ReservationsTable.selectAll()
            .where { (ReservationsTable.id eq id) and (ReservationsTable.status eq ReservationStatus.ACTIVE.name) }
            .map { it.toReservation() }
            .singleOrNull()
    }

    fun create(kittenId: Int, buyerId: Int): Reservation = transaction {
        val id = ReservationsTable.insert {
            it[ReservationsTable.kittenId] = kittenId
            it[ReservationsTable.buyerId] = buyerId
            it[ReservationsTable.reservedAt] = Instant.now()
            it[ReservationsTable.status] = ReservationStatus.ACTIVE.name
        } get ReservationsTable.id

        ReservationsTable.selectAll()
            .where { ReservationsTable.id eq id }
            .map { it.toReservation() }
            .single()
    }

    fun cancel(id: Int): Boolean = transaction {
        ReservationsTable.update({ ReservationsTable.id eq id }) {
            it[ReservationsTable.status] = ReservationStatus.CANCELLED.name
        } > 0
    }

    fun cancelActiveByKitten(kittenId: Int): Boolean = transaction {
        ReservationsTable.update({
            (ReservationsTable.kittenId eq kittenId) and (ReservationsTable.status eq ReservationStatus.ACTIVE.name)
        }) {
            it[ReservationsTable.status] = ReservationStatus.CANCELLED.name
        } > 0
    }

    fun findForBuyer(buyerId: Int): List<ReservationDetailResponse> = transaction {
        (ReservationsTable innerJoin KittensTable innerJoin LittersTable innerJoin UsersTable)
            .select(
                ReservationsTable.id,
                ReservationsTable.kittenId,
                ReservationsTable.buyerId,
                ReservationsTable.reservedAt,
                ReservationsTable.status,
                KittensTable.name,
                KittensTable.photoUrls,
                LittersTable.id,
                LittersTable.name,
                UsersTable.name,
            )
            .where {
                (ReservationsTable.buyerId eq buyerId) and
                    (ReservationsTable.status eq ReservationStatus.ACTIVE.name) and
                    (UsersTable.id eq ReservationsTable.buyerId)
            }
            .orderBy(ReservationsTable.reservedAt to SortOrder.DESC)
            .map { it.toDetail() }
    }

    fun findForBreeder(ownerId: Int): List<ReservationDetailResponse> = transaction {
        (ReservationsTable innerJoin KittensTable innerJoin LittersTable innerJoin UsersTable)
            .select(
                ReservationsTable.id,
                ReservationsTable.kittenId,
                ReservationsTable.buyerId,
                ReservationsTable.reservedAt,
                ReservationsTable.status,
                KittensTable.name,
                KittensTable.photoUrls,
                LittersTable.id,
                LittersTable.name,
                UsersTable.name,
            )
            .where {
                (LittersTable.ownerId eq ownerId) and
                    (ReservationsTable.status eq ReservationStatus.ACTIVE.name) and
                    (UsersTable.id eq ReservationsTable.buyerId)
            }
            .orderBy(ReservationsTable.reservedAt to SortOrder.DESC)
            .map { it.toDetail() }
    }

    private fun ResultRow.toReservation() = Reservation(
        id = this[ReservationsTable.id].toLong(),
        kittenId = this[ReservationsTable.kittenId].toLong(),
        buyerId = this[ReservationsTable.buyerId].toLong(),
        reservedAt = this[ReservationsTable.reservedAt].toString(),
        status = ReservationStatus.valueOf(this[ReservationsTable.status]),
    )

    private fun ResultRow.toDetail() = ReservationDetailResponse(
        id = this[ReservationsTable.id].toLong(),
        kittenId = this[ReservationsTable.kittenId].toLong(),
        kittenName = this[KittensTable.name],
        kittenPhotoUrls = PhotoUrlsJson.decode(this[KittensTable.photoUrls]),
        litterId = this[LittersTable.id].toLong(),
        litterName = this[LittersTable.name],
        buyerId = this[ReservationsTable.buyerId].toLong(),
        buyerName = this[UsersTable.name],
        reservedAt = this[ReservationsTable.reservedAt].toString(),
        status = ReservationStatus.valueOf(this[ReservationsTable.status]),
    )
}
