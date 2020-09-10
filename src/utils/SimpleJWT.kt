package com.josancamon19.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class SimpleJWT(secret: String = SimpleJWT.secret) {
    private val validityInMs = 36_000_00 * 1
    private val algorithm = Algorithm.HMAC256(secret)

    fun sign(name: String): String = JWT.create()
        .withClaim("name", name)
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    companion object {
        private const val secret = "josancamon19123"
        private const val jwtIssuer = "https://jwt-provider-domain/"
        const val jwtAudience = "jwt-audience"
        const val jwtRealm = "ktor sample app"

        fun makeJwtVerifier(): JWTVerifier = JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .build()
    }
}