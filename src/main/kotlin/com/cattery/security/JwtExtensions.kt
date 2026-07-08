package com.cattery.security

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.principalUserId(): Int {
    principal<UserPrincipal>()?.let { return it.userId.toInt() }

    val jwt = principal<JWTPrincipal>()
        ?: throw IllegalArgumentException("Требуется авторизация")
    val userId = jwt.payload.getClaim("userId").asLong()
        ?: jwt.payload.getClaim("userId").asInt()?.toLong()
        ?: throw IllegalArgumentException("Некорректный токен: отсутствует userId")
    return userId.toInt()
}

fun ApplicationCall.principalRole(): String {
    principal<UserPrincipal>()?.let { return it.role }

    val jwt = principal<JWTPrincipal>()
        ?: throw IllegalArgumentException("Требуется авторизация")
    return jwt.payload.getClaim("role").asString()
        ?: throw IllegalArgumentException("Некорректный токен: отсутствует role")
}

fun ApplicationCall.requireBreeder() {
    if (principalRole() != "BREEDER") {
        throw IllegalArgumentException("Доступ только для заводчика")
    }
}

fun ApplicationCall.requireBuyer() {
    if (principalRole() != "BUYER") {
        throw IllegalArgumentException("Доступ только для покупателя")
    }
}
