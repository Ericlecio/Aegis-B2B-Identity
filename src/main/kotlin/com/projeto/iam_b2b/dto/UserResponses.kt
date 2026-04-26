package com.projeto.iam_b2b.dto

import java.util.UUID

data class TokenResponse(
    val accessToken: String,
    val type: String = "Bearer"
)

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String
)