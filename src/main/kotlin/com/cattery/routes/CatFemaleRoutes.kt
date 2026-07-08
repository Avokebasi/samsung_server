package com.cattery.routes

import com.cattery.models.UserRole
import com.cattery.requests.SaveCatFemaleRequest
import com.cattery.security.principalRole
import com.cattery.security.principalUserId
import com.cattery.security.requireBreeder
import com.cattery.services.CatFemaleService
import com.cattery.services.LitterService
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

fun Route.catFemaleRoutes(service: CatFemaleService, litterService: LitterService) {
    route("cat-females") {
        authenticate("auth-jwt") {
            get {
                val userId = call.principalUserId()
                val role = UserRole.valueOf(call.principalRole())
                call.respond(service.list(userId, role))
            }
            get("search") {
                val userId = call.principalUserId()
                val role = UserRole.valueOf(call.principalRole())
                val query = call.request.queryParameters["q"].orEmpty()
                call.respond(service.search(userId, role, query))
            }
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                call.respond(service.getById(id))
            }
            get("{id}/litters") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                service.getById(id)
                call.respond(litterService.findByMotherId(id))
            }
            post {
                call.requireBreeder()
                val userId = call.principalUserId()
                val request = call.receive<SaveCatFemaleRequest>()
                call.respond(HttpStatusCode.Created, service.create(userId, request))
            }
            put("{id}") {
                call.requireBreeder()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                val request = call.receive<SaveCatFemaleRequest>()
                call.respond(service.update(userId, id, request))
            }
            delete("{id}") {
                call.requireBreeder()
                val userId = call.principalUserId()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Некорректный id")
                service.delete(userId, id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
