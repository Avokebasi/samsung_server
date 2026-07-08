package com.cattery.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(
        jdbcUrl: String,
        driver: String,
        user: String,
        password: String,
        maxPoolSize: Int,
        flywayLocations: String,
    ) {
        val dataSource = HikariDataSource(
            HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.driverClassName = driver
                this.username = user
                this.password = password
                this.maximumPoolSize = maxPoolSize
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            },
        )

        try {
            Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayLocations)
                .load()
                .migrate()
        } catch (e: Exception) {
            if (jdbcUrl.contains("postgresql")) {
                throw IllegalStateException(
                    """
                    Не удалось подключиться к PostgreSQL ($jdbcUrl).
                    Пользователь/пароль cattery не созданы или PostgreSQL не запущен.
                    
                    Для разработки без PostgreSQL: gradlew run
                    Для PostgreSQL: docker compose up -d или scripts/setup-postgres.sql
                    """.trimIndent(),
                    e,
                )
            }
            throw e
        }

        Database.connect(dataSource)
    }
}
