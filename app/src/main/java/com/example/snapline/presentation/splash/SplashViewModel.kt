package com.example.snapline.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.domain.use_case.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
): ViewModel() {

    private val _token = Channel<String?>()
    val token = _token.receiveAsFlow()

    fun getToken() {
        viewModelScope.launch {
            authUseCases
                .getTokenUseCase()
                .collectLatest {
                    _token.send(it)
                }
        }
    }
}