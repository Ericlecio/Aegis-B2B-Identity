package com.projeto.iam_b2b.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.time.Duration

@Service
class RefreshTokenService(private val redisTemplate: StringRedisTemplate) {

    // ID único e salva no Redis vinculado ao ID do usuário
    fun createRefreshToken(userId: UUID): String {
        val refreshToken = UUID.randomUUID().toString()
        // Salva: "refresh_token:UUID" -> "USER_ID" por 7 dias
        redisTemplate.opsForValue().set(
            "refresh:$refreshToken",
            userId.toString(),
            Duration.ofDays(7)
        )
        return refreshToken
    }

    fun getUserIdFromToken(token: String): UUID? {
        val userId = redisTemplate.opsForValue().get("refresh:$token")
        return userId?.let { UUID.fromString(it) }
    }

    fun deleteRefreshToken(token: String) {
        redisTemplate.delete("refresh:$token")
    }
}