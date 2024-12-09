package com.example.snapline.presentation.story_add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapline.data.remote.response.UploadStoryResponse
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
class StoryAddViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases,
) : ViewModel() {

    private val _uriImage = MutableStateFlow<Uri?>(null)
    val uriImage: StateFlow<Uri?> = _uriImage

    private val _uploadStoryData = Channel<UploadStoryResponse?>()
    val uploadStoryData = _uploadStoryData.receiveAsFlow()
    private val _uploadStoryLoading = MutableStateFlow(false)
    val uploadStoryLoading: StateFlow<Boolean> = _uploadStoryLoading
    private val _uploadStoryError = Channel<UiText>()
    val uploadStoryError = _uploadStoryError.receiveAsFlow()

    fun setUriImage(uri: Uri) {
        _uriImage.value = uri
    }

    fun uploadStory(
        description: String,
        latitude: Float?,
        longitude: Float?,
    ) {
        viewModelScope.launch {
            storyUseCases
                .uploadStoryUseCase(
                    uri = _uriImage.value,
                    description = description,
                    latitude = latitude,
                    longitude = longitude
                )
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _uploadStoryError.send(result.uiText ?: UiText.unknownError())
                            _uploadStoryLoading.value = false
                        }

                        is Resource.Loading -> {
                            _uploadStoryLoading.value = true
                        }

                        is Resource.Success -> {
                            _uploadStoryLoading.value = false
                            _uploadStoryData.send(result.data)
                        }
                    }
                }
        }
    }
}