package com.projeto.iam_b2b.controller

import com.projeto.iam_b2b.domain.User
import com.projeto.iam_b2b.domain.UserRole
import com.projeto.iam_b2b.dto.UserResponse
import com.projeto.iam_b2b.repository.UserRepository
import com.projeto.iam_b2b.security.TenantContext
import com.projeto.iam_b2b.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/me")
    fun getMe(): ResponseEntity<Any> {
        val tenantId = TenantContext.getTenantId() ?: return ResponseEntity.status(403).build()
        val users = userService.listAllFromCurrentTenant()

        return ResponseEntity.ok(mapOf(
            "empresa_logada" to tenantId,
            "usuarios_da_minha_empresa" to users.map {
                mapOf("id" to it.id, "name" to it.name, "email" to it.email, "role" to it.role)
            }
        ))
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')") // Proteção RBAC
    fun create(@RequestBody request: CreateUserRequest): ResponseEntity<Any> {
        val currentTenant = TenantContext.getTenantId() ?: return ResponseEntity.status(403).build()

        val newUser = User(
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)!!, // !! resolve o erro da imagem
            tenantId = currentTenant,
            role = UserRole.USER
        )

        userRepository.save(newUser)
        return ResponseEntity.ok(mapOf("message" to "Usuário criado com sucesso!"))
    }
}

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String
)