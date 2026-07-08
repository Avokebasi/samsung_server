package com.cattery.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 100).uniqueIndex()
    val passwordHash = text("password_hash")
    val name = varchar("name", 200)
    val avatarUrl = text("avatar_url").nullable()
    val role = varchar("role", 20)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object CatFemalesTable : Table("cat_females") {
    val id = integer("id").autoIncrement()
    val ownerId = integer("owner_id").references(UsersTable.id)
    val name = varchar("name", 200)
    val birthDate = date("birth_date")
    val matingDate = date("mating_date").nullable()
    val birthDueDate = date("birth_due_date").nullable()
    val photoUrls = text("photo_urls")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object CatMalesTable : Table("cat_males") {
    val id = integer("id").autoIncrement()
    val ownerId = integer("owner_id").references(UsersTable.id)
    val name = varchar("name", 200)
    val birthDate = date("birth_date")
    val photoUrls = text("photo_urls")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object LittersTable : Table("litters") {
    val id = integer("id").autoIncrement()
    val ownerId = integer("owner_id").references(UsersTable.id)
    val name = varchar("name", 10)
    val birthDate = date("birth_date")
    val totalCount = integer("total_count")
    val maleCount = integer("male_count")
    val femaleCount = integer("female_count")
    val motherId = integer("mother_id").references(CatFemalesTable.id).nullable()
    val fatherId = integer("father_id").references(CatMalesTable.id).nullable()
    val photoUrls = text("photo_urls")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object KittensTable : Table("kittens") {
    val id = integer("id").autoIncrement()
    val litterId = integer("litter_id").references(LittersTable.id)
    val name = varchar("name", 200)
    val birthDate = date("birth_date")
    val color = varchar("color", 200)
    val birthWeight = decimal("birth_weight", 5, 2).nullable()
    val status = varchar("status", 20)
    val photoUrls = text("photo_urls")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

object ReservationsTable : Table("reservations") {
    val id = integer("id").autoIncrement()
    val kittenId = integer("kitten_id").references(KittensTable.id)
    val buyerId = integer("buyer_id").references(UsersTable.id)
    val reservedAt = timestamp("reserved_at")
    val status = varchar("status", 20)

    override val primaryKey = PrimaryKey(id)
}
