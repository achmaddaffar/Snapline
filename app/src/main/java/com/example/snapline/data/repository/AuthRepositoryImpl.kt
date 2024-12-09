package com.example.snapline.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.snapline.data.remote.network.ApiService
import com.example.snapline.data.remote.response.LoginResponse
import com.example.snapline.data.remote.response.RegisterResponse
import com.example.snapline.data.util.GlobalErrorParser
import com.example.snapline.domain.model.User
import com.example.snapline.domain.repository.AuthRepository
import com.example.snapline.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>,
    private val errorParser: GlobalErrorParser
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): RegisterResponse {
        val response = apiService.register(
            name = name,
            email = email,
            password = password
        )
        if (response.isSuccessful) {
            response.body()?.let { data -> return data }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun login(
        email: String,
        password: String,
    ): LoginResponse {
        val response = apiService.login(
            email = email,
            password = password
        )
        if (response.isSuccessful) {
            val responseBody = response.body()
            saveToken(responseBody?.loginResult?.token.toString())
            saveUser(
                User(
                    name = responseBody?.loginResult?.name.toString(),
                    email = email
                )
            )
            responseBody?.let { data -> return data }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[Constants.TOKEN_KEY]
        }
    }

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[Constants.NAME_KEY] ?: "",
                preferences[Constants.EMAIL_KEY] ?: ""
            )
        }
    }

    private suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[Constants.NAME_KEY] = user.name
            preferences[Constants.EMAIL_KEY] = user.email
        }
    }

    private suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[Constants.TOKEN_KEY] = token
        }
    }

    override suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences[Constants.NAME_KEY] = ""
            preferences[Constants.EMAIL_KEY] = ""
            preferences[Constants.TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        @JvmStatic
        fun getInstance(
            apiService: ApiService,
            dataStore: DataStore<Preferences>,
            errorParser: GlobalErrorParser
        ): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepositoryImpl(
                    apiService = apiService,
                    dataStore = dataStore,
                    errorParser = errorParser
                )
            }
        }
    }
}