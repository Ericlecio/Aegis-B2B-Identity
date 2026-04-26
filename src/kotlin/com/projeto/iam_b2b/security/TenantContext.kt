package com.projeto.iam_b2b.security

import java.util.UUID

object TenantContext {
    private val tenantId = ThreadLocal<UUID>()

    fun setTenantId(id: UUID) { tenantId.set(id) }

    fun getTenantId(): UUID? = tenantId.get()

    fun clear() { tenantId.remove() } // evitar vazamentos entre requisições simultâneas
}