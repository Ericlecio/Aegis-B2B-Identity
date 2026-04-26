package com.projeto.iam_b2b.controller

import com.projeto.iam_b2b.dto.LoginRequest
import com.projeto.iam_b2b.dto.TokenResponse
import com.projeto.iam_b2b.service.AuthService
import com.projeto.iam_b2b.service.RefreshTokenService
import com.projeto.iam_b2b.service.TokenBlacklistService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.Cookie

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
    private val tokenBlacklistService: TokenBlacklistService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val (accessToken, refreshToken) = authService.login(request.email, request.passwordRaw)

        // Criando o Cookie HttpOnly
        val cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return ResponseEntity.ok(TokenResponse(accessToken = accessToken))
    }

    @PostMapping("/refresh")
    fun refresh(@CookieValue("refreshToken") refreshToken: String, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val (newAccess, newRefresh) = authService.refresh(refreshToken)

        val cookie = ResponseCookie.from("refreshToken", newRefresh)
            .httpOnly(true).secure(false).path("/").maxAge(7 * 24 * 60 * 60).sameSite("Lax").build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity.ok(TokenResponse(accessToken = newAccess))
    }
}