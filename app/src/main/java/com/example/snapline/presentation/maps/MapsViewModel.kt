package com.example.snapline.presentation.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.data.remote.response.GetStoryResponse
import com.example.snapline.domain.use_case.story.StoryUseCases
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
class MapsViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases
) : ViewModel() {

    private val _storyLoading = MutableStateFlow(false)
    val storyLoading: StateFlow<Boolean> = _storyLoading
    private val _storyError = Channel<UiText>()
    val storyError = _storyError.receiveAsFlow()
    private val _storyData = Channel<GetStoryResponse?>()
    val storyData = _storyData.receiveAsFlow()

    init {
        getStoryWithLocation()
    }

    private fun getStoryWithLocation() {
        viewModelScope.launch {
            storyUseCases
                .getStoryUseCase()
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _storyLoading.value = false
                            _storyError.send(result.uiText ?: UiText.unknownError())
                        }

                        is Resource.Loading -> {
                            _storyLoading.value = true
                        }

                        is Resource.Success -> {
                            _storyLoading.value = false
                            _storyData.send(result.data)
                        }
                    }
                }
        }
    }
}