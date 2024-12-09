package com.example.snapline.domain.use_case.auth

import com.example.snapline.domain.use_case.creator.BaseUseCases

data class AuthUseCases(
    val loginUserUseCase: LoginUserUseCase,
    val logoutUserUseCase: LogoutUserUseCase,
    val registerUserUseCase: RegisterUserUseCase,
    val getTokenUseCase: GetTokenUseCase
): BaseUseCases
