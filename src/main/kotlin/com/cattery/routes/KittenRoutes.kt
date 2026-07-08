package com.cattery.routes

import com.cattery.models.UserRole
import com.cattery.requests.SaveKittenRequest
import com.cattery.security.principalRole
import com.cattery.security.principalUserId
import com.cattery.security.requireBreeder
import com.cattery.security.requireBuyer
import com.cattery.services.KittenService
import com.cattery.services.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.kittenRoutes(kittenService: KittenService, reservationService: ReservationService) {
    route("kittens") {
        authenticate("auth-jwt") {
            get("search") {
                val userId = call.principalUserId()
                val role = UserRole.valueOf(call.principalRole())
                val query = call.request.queryParameters["q"].orEmpty()
                call.respond(kittenService.search(userId, role, query))
            }
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                call.respond(kittenService.getDetail(id))
            }
            post {
                call.requireBreeder()
                val userId = call.principalUserId()
                val request = call.receive<SaveKittenRequest>()
                call.respond(HttpStatusCode.Created, kittenService.create(userId, request))
            }
            put("{id}") {
                call.requireBreeder()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                val request = call.receive<SaveKittenRequest>()
                call.respond(kittenService.update(userId, id, request))
            }
            delete("{id}") {
                call.requireBreeder()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                kittenService.delete(userId, id)
                call.respond(HttpStatusCode.NoContent)
            }
            post("{id}/reserve") {
                call.requireBuyer()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                call.respond(HttpStatusCode.Created, reservationService.reserve(userId, id))
            }
            delete("{id}/reserve") {
                call.requireBuyer()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                reservationService.cancel(userId, id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
