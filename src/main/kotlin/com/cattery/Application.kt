package com.cattery

import com.cattery.plugins.configureDatabase
import com.cattery.plugins.configureRouting
import com.cattery.plugins.configureSecurity
import com.cattery.plugins.configureSerialization
import com.cattery.plugins.configureStatusPages
import com.cattery.security.JwtService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val config = environment.config

    val jwtService = JwtService(
        secret = config.property("jwt.secret").getString(),
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
        expirationMs = config.property("jwt.expirationMs").getString().toLong(),
        realm = config.property("jwt.realm").getString(),
    )

    install(CallLogging)

    configureDatabase()
    configureSerialization()
    configureSecurity(jwtService)
    configureStatusPages()
    configureRouting(jwtService)
}
