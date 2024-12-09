package com.example.snapline.domain.use_case.auth

import com.example.snapline.domain.repository.AuthRepository

class LogoutUserUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() = repository.deleteUser()
}