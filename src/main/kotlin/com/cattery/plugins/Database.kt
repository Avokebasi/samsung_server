package com.cattery.plugins

import com.cattery.database.DatabaseFactory
import io.ktor.server.application.Application

fun Application.configureDatabase() {
    val config = environment.config
    val jdbcUrl = config.property("database.url").getString()
    val maskedUrl = jdbcUrl.replace(Regex("password=[^&;]*", RegexOption.IGNORE_CASE), "password=***")
    environment.log.info("Подключение к БД: $maskedUrl")
    DatabaseFactory.init(
        jdbcUrl = jdbcUrl,
        driver = config.property("database.driver").getString(),
        user = config.property("database.user").getString(),
        password = config.property("database.password").getString(),
        maxPoolSize = config.property("database.maxPoolSize").getString().toInt(),
        flywayLocations = config.property("flyway.locations").getString(),
    )
}
