package com.example.snapline.domain.use_case.auth

import com.example.snapline.data.remote.response.RegisterResponse
import com.example.snapline.domain.repository.AuthRepository
import com.example.snapline.util.Resource
import com.example.snapline.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RegisterUserUseCase(
    private val repository: AuthRepository
) {

    operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<RegisterResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.register(
                name = name,
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