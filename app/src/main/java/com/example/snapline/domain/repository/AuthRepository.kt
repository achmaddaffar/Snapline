package com.example.snapline.domain.repository

import com.example.snapline.data.remote.response.LoginResponse
import com.example.snapline.data.remote.response.RegisterResponse
import com.example.snapline.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse

    suspend fun deleteUser()

    fun getToken(): Flow<String?>
}