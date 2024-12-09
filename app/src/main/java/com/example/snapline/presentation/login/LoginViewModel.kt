package com.example.snapline.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.data.remote.response.LoginResponse
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
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
) : ViewModel() {

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading

    private val _loginError = Channel<UiText>()
    val loginError = _loginError.receiveAsFlow()

    private val _loginData = Channel<LoginResponse?>()
    val loginData = _loginData.receiveAsFlow()

    fun login(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            authUseCases
                .loginUserUseCase(email, password)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _loginError.send(result.uiText ?: UiText.unknownError())
                            _loginLoading.value = false
                        }

                        is Resource.Loading -> {
                            _loginLoading.value = true
                        }

                        is Resource.Success -> {
                            _loginData.send(result.data)
                            _loginLoading.value = false
                        }
                    }
                }
        }
    }
}