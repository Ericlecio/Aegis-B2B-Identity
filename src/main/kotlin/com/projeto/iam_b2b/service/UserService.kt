package com.projeto.iam_b2b.service

import com.projeto.iam_b2b.domain.User
import com.projeto.iam_b2b.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional(readOnly = true)
    fun listAllFromCurrentTenant(): List<User> {
        return userRepository.findAll()
    }
}