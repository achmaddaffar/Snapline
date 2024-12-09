package com.example.snapline.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.data.remote.response.RegisterResponse
import com.example.snapline.domain.use_case.auth.AuthUseCases
import com.example.snapline.util.Resource
import com.example.snapline.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
): ViewModel() {

    private val _registerLoading = MutableStateFlow(false)
    val registerLoading: StateFlow<Boolean> = _registerLoading

    private val _registerError = Channel<UiText>()
    val registerError = _registerError.receiveAsFlow()

    private val _registerData = Channel<RegisterResponse?>()
    val registerData = _registerData.receiveAsFlow()

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            authUseCases
                .registerUserUseCase(
                    name = name,
                    email = email,
                    password = password
                )
                .collect { result ->
                    Log.e("OKHT", result.toString())
                    when(result) {
                        is Resource.Error -> {
                            _registerError.send(result.uiText ?: UiText.unknownError())
                            _registerLoading.value = false
                        }

                        is Resource.Loading -> {
                            _registerLoading.value = true
                        }

                        is Resource.Success -> {
                            _registerData.send(result.data)
                            _registerLoading.value = false
                        }
                    }
                }
        }
    }
}