package com.cattery.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val expirationMs: Long,
    val realm: String,
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: Long, login: String, role: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("login", login)
            .withClaim("role", role)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + expirationMs))
            .sign(algorithm)
    }

    fun verifier() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
