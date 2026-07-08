package com.cattery.database.dao

import com.cattery.database.tables.UsersTable
import com.cattery.models.User
import com.cattery.models.UserRole
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class UserDao {
    fun findByLogin(login: String): UserRecord? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.login eq login }
            .map { it.toRecord() }
            .singleOrNull()
    }

    fun findById(id: Int): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    fun create(login: String, passwordHash: String, name: String, role: UserRole): User = transaction {
        val id = UsersTable.insert {
            it[UsersTable.login] = login
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.name] = name
            it[UsersTable.role] = role.name
            it[createdAt] = Instant.now()
        } get UsersTable.id

        User(
            id = id.toLong(),
            username = login,
            name = name,
            role = role,
        )
    }

    fun updateAvatar(userId: Int, avatarUrl: String?): Boolean = transaction {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.avatarUrl] = avatarUrl
        } > 0
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].toLong(),
        username = this[UsersTable.login],
        name = this[UsersTable.name],
        avatarUrl = this[UsersTable.avatarUrl],
        role = UserRole.valueOf(this[UsersTable.role]),
    )

    private fun ResultRow.toRecord() = UserRecord(
        id = this[UsersTable.id],
        login = this[UsersTable.login],
        passwordHash = this[UsersTable.passwordHash],
        name = this[UsersTable.name],
        avatarUrl = this[UsersTable.avatarUrl],
        role = UserRole.valueOf(this[UsersTable.role]),
    )
}

data class UserRecord(
    val id: Int,
    val login: String,
    val passwordHash: String,
    val name: String,
    val avatarUrl: String?,
    val role: UserRole,
)
