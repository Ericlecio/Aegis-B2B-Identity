package com.projeto.iam_b2b.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenBlacklistService(private val redisTemplate: StringRedisTemplate) {

    fun blacklistToken(token: String, expirationSeconds: Long) {
        redisTemplate.opsForValue().set(token, "revoked", Duration.ofSeconds(expirationSeconds))
    }

    fun isBlacklisted(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}