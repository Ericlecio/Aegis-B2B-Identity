package com.projeto.iam_b2b.service

import com.projeto.iam_b2b.domain.User
import com.projeto.iam_b2b.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val refreshTokenService: RefreshTokenService
) {

    fun login(email: String, passwordRaw: String): Pair<String, String> {
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Credenciais inválidas")

        if (!passwordEncoder.matches(passwordRaw, user.passwordHash)) {
            throw RuntimeException("Credenciais inválidas")
        }

        // Gera o Access Token (JWT) -  15 min
        val accessToken = generateAccessToken(user)

        // Gera o Refresh Token - O passe de 7 dias
        val refreshToken = refreshTokenService.createRefreshToken(user.id!!)

        return Pair(accessToken, refreshToken)
    }

    private fun generateAccessToken(user: User): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("iam-b2b-api")
            .subject(user.id.toString())
            .claim("tenant_id", user.tenantId.toString())
            .claim("role", "ROLE_${user.role.name}")
            .build()

        return jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(claims)).tokenValue
    }

    fun refresh(refreshToken: String): Pair<String, String> {
        val userId = refreshTokenService.getUserIdFromToken(refreshToken)
            ?: throw RuntimeException("Refresh Token inválido ou expirado")

        val user = userRepository.findById(userId).orElseThrow()

        // Rotação: Apaga o antigo e gera um novo par
        refreshTokenService.deleteRefreshToken(refreshToken)
        val newAccessToken = generateAccessToken(user)
        val newRefreshToken = refreshTokenService.createRefreshToken(user.id!!)

        return Pair(newAccessToken, newRefreshToken)
    }
}