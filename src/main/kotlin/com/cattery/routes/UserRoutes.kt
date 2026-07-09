package com.cattery.routes

import com.cattery.requests.UpdateAvatarRequest
import com.cattery.security.principalUserId
import com.cattery.services.UserService
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.userRoutes(userService: UserService) {
    route("users") {
        authenticate("auth-jwt") {
            put("me/avatar") {
                val userId = call.principalUserId()
                val request = call.receive<UpdateAvatarRequest>()
                call.respond(userService.updateAvatar(userId, request.avatarUrl))
            }
        }
    }
}
