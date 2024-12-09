package com.example.snapline.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.domain.use_case.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            authUseCases.logoutUserUseCase()
        }
    }
}