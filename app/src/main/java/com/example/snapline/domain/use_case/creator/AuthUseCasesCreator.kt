package com.example.snapline.domain.use_case.creator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.snapline.data.remote.network.ApiService
import com.example.snapline.data.repository.AuthRepositoryImpl
import com.example.snapline.data.util.GlobalErrorParser
import com.example.snapline.domain.use_case.auth.AuthUseCases
import com.example.snapline.domain.use_case.auth.GetTokenUseCase
import com.example.snapline.domain.use_case.auth.LoginUserUseCase
import com.example.snapline.domain.use_case.auth.LogoutUserUseCase
import com.example.snapline.domain.use_case.auth.RegisterUserUseCase
import com.google.gson.Gson

class AuthUseCasesCreator: BaseUseCasesCreator() {
    override fun create(
        context: Context,
        dataStore: DataStore<Preferences>
    ): BaseUseCases {
        val apiService = ApiService.getInstance()
        val errorParser = GlobalErrorParser(Gson())

        val authRepository = AuthRepositoryImpl.getInstance(
            apiService = apiService,
            dataStore = dataStore,
            errorParser = errorParser
        )

        return AuthUseCases(
            loginUserUseCase = LoginUserUseCase(authRepository),
            logoutUserUseCase = LogoutUserUseCase(authRepository),
            registerUserUseCase = RegisterUserUseCase(authRepository),
            getTokenUseCase = GetTokenUseCase(authRepository)
        )
    }
}