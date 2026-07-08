package com.cattery.routes

import com.cattery.models.UserRole
import com.cattery.security.principalRole
import com.cattery.security.principalUserId
import com.cattery.services.ReservationService
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.reservationRoutes(service: ReservationService) {
    route("reservations") {
        authenticate("auth-jwt") {
            get {
                val userId = call.principalUserId()
                val role = UserRole.valueOf(call.principalRole())
                call.respond(service.list(userId, role))
            }
        }
    }
}
