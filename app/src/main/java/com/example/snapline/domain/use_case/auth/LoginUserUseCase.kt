package com.example.snapline.domain.use_case.auth

import com.example.snapline.data.remote.response.LoginResponse
import com.example.snapline.domain.repository.AuthRepository
import com.example.snapline.util.Resource
import com.example.snapline.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginUserUseCase(
    private val repository: AuthRepository,
) {

    operator fun invoke(
        email: String,
        password: String,
    ): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.login(
                email = email,
                password = password
            )

            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message ?: e.toString())))
        }
    }
        .flowOn(Dispatchers.IO)
}