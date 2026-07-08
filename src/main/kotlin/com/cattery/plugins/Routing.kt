package com.cattery.plugins

import com.cattery.database.dao.UserDao
import com.cattery.routes.authRoutes
import com.cattery.security.JwtService
import com.cattery.services.AuthService
import com.cattery.services.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(jwtService: JwtService) {
    val userDao = UserDao()
    val authService = AuthService(userDao, jwtService)
    val userService = UserService(userDao)

    routing {
        get("/") {
            call.respondText("Cattery API")
        }
        authRoutes(authService, userService)
    }
}
