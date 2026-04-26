package com.projeto.iam_b2b.repository

import com.projeto.iam_b2b.domain.Organization
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrganizationRepository : JpaRepository<Organization, UUID> {
    fun findByDocument(document: String): Organization?
}