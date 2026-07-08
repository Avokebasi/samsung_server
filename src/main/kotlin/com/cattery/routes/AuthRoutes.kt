package com.cattery.routes

import com.cattery.requests.LoginRequest
import com.cattery.requests.RegisterRequest
import com.cattery.security.principalUserId
import com.cattery.services.AuthService
import com.cattery.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService, userService: UserService) {
    route("auth") {
        post("login") {
            val request = call.receive<LoginRequest>()
            call.respond(authService.login(request))
        }
        post("register") {
            val request = call.receive<RegisterRequest>()
            call.respond(HttpStatusCode.Created, authService.register(request))
        }
        authenticate("auth-jwt") {
            get("me") {
                call.respond(userService.getById(call.principalUserId()))
            }
            post("logout") {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
