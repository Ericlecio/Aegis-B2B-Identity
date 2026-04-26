package com.projeto.iam_b2b

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.projeto.iam_b2b.controller.CreateUserRequest
import com.projeto.iam_b2b.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class UserAuditTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userRepository: UserRepository

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    @Test
    fun `deve preencher campos de auditoria ao criar usuario`() {
        val userId = "2e39d1ac-da4d-401f-a057-0da0194504fc"
        val tenantId = UUID.randomUUID().toString()

        val request = CreateUserRequest("Peter Parker", "spidey@stark.com", "senha123")

        mockMvc.post("/users/create") {
            // AQUI ESTÁ O SEGREDO: Simulamos o JWT com ID do usuário, ID do Tenant e a ROLE
            with(jwt().jwt { it.subject(userId).claim("tenant_id", tenantId) }
                .authorities(SimpleGrantedAuthority("ROLE_ADMIN")))

            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }

        val criado = userRepository.findByEmail("spidey@stark.com")

        assertNotNull(criado)
        assertNotNull(criado?.createdAt)
        assertEquals(userId, criado?.createdBy.toString())
    }
}