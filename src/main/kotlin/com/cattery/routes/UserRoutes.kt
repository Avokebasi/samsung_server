package com.cattery.routes

import com.cattery.security.principalUserId
import com.cattery.services.UserService
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.userRoutes(userService: UserService) {
    route("users") {
        authenticate("auth-jwt") {
            put("me/avatar") {
                val userId = call.principalUserId()
                val params = call.receiveParameters()
                val avatarUrl = params["avatarUrl"]
                call.respond(userService.updateAvatar(userId, avatarUrl))
            }
        }
    }
}
