package com.projeto.iam_b2b.security

import com.projeto.iam_b2b.service.TokenBlacklistService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class TenantFilter(
    private val tokenBlacklistService: TokenBlacklistService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Verificação de Segurança (Redis Blacklist)
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            if (tokenBlacklistService.isBlacklisted(token)) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Token revogado (Blacklisted). Por favor, faca login novamente.")
                return // Mata a requisição aqui mesmo
            }
        }

        // Extração do Contexto Multi-Tenant
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.principal is Jwt) {
            val jwt = authentication.principal as Jwt
            val tenantIdStr = jwt.getClaimAsString("tenant_id")

            if (tenantIdStr != null) {
                // Seta o ID da empresa para que o Hibernate saiba o que filtrar no banco
                TenantContext.setTenantId(UUID.fromString(tenantIdStr))
            }
        }

        try {
            // Segue o fluxo normal da requisição
            filterChain.doFilter(request, response)
        } finally {
            // Limpeza obrigatória para não vazar ID de uma thread para outra
            TenantContext.clear()
        }
    }
}