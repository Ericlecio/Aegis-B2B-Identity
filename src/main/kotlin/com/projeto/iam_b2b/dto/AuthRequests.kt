package com.projeto.iam_b2b.dto


data class LoginRequest(
    val email: String,
    val passwordRaw: String
)
data class AuthResponse(
    val accessToken: String,
    val expiresIn: Long
    // O Refresh Token NÃO vai aqui no JSON de propósito para evitar XSS!
)