package com.cattery.plugins

import com.cattery.responses.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import java.sql.SQLException

fun Application.configureStatusPages() {
    val isDev = developmentMode

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        exception<IllegalStateException> { call, cause ->
            call.application.environment.log.error("State error", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(cause.message ?: "Ошибка состояния сервера"),
            )
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        exception<SQLException> { call, cause ->
            call.application.environment.log.error("Database error", cause)
            val hint = when {
                cause.message?.contains("does not exist", ignoreCase = true) == true ->
                    "Таблицы не созданы. Перезапустите сервер (Flyway)."

                cause.message?.contains("foreign key", ignoreCase = true) == true ->
                    "Связанная запись не найдена."

                else -> cause.message
            }
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    if (isDev) hint ?: "Ошибка базы данных" else "Ошибка базы данных",
                ),
            )
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    if (isDev) {
                        "${cause::class.simpleName}: ${cause.message}"
                    } else {
                        "Внутренняя ошибка сервера"
                    },
                ),
            )
        }
    }
}
