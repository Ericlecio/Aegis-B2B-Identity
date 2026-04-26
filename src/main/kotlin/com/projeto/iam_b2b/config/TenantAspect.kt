package com.projeto.iam_b2b.config

import com.projeto.iam_b2b.security.TenantContext
import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Aspect
@Component
class TenantAspect(private val entityManager: EntityManager) {

    @Before("@within(org.springframework.transaction.annotation.Transactional) || @annotation(org.springframework.transaction.annotation.Transactional)")
    fun beforeTransactionalMethod() {
        val tenantId = TenantContext.getTenantId()
        if (tenantId != null) {
            val session = entityManager.unwrap(Session::class.java)
            val filter = session.enableFilter("tenantFilter")
            filter.setParameter("tenantId", tenantId)
        }
    }
}