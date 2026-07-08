package com.cattery.security

import io.ktor.server.auth.Principal

data class UserPrincipal(
    val userId: Long,
    val login: String,
    val role: String,
) : Principal
