package com.cattery.plugins

import com.cattery.security.JwtService
import com.cattery.security.UserPrincipal
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(jwtService: JwtService) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtService.realm
            verifier(jwtService.verifier())
            validate { credential ->
                val payload = credential.payload
                val userId = payload.getClaim("userId").asLong()
                val login = payload.getClaim("login").asString()
                val role = payload.getClaim("role").asString()
                if (userId != null && login != null && role != null) {
                    UserPrincipal(userId, login, role)
                } else {
                    null
                }
            }
        }
    }
}
