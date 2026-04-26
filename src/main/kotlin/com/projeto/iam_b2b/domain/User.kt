package com.projeto.iam_b2b.domain

import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.ParamDef
import java.util.UUID

@Entity
@Table(name = "users")

@FilterDef(
    name = "tenantFilter",
    parameters = [ParamDef(name = "tenantId", type = UUID::class)]
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(name = "tenant_id", nullable = false)
    val tenantId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.USER
): BaseEntity()