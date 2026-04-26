package com.projeto.iam_b2b.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.util.*

@Configuration
@EnableJpaAuditing
class AuditConfig {

    @Bean
    fun auditorProvider(): AuditorAware<UUID> {
        return AuditorAware {
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication != null && authentication.principal is Jwt) {
                val jwt = authentication.principal as Jwt
                Optional.of(UUID.fromString(jwt.subject))
            } else {
                Optional.empty()
            }
        }
    }
}