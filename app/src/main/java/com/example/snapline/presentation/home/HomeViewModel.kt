package com.example.snapline.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.snapline.domain.model.StoryItem
import com.example.snapline.domain.use_case.story.StoryUseCases
import com.example.snapline.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases
) : ViewModel() {

    private val _storyData = MutableStateFlow<PagingData<StoryItem>>(PagingData.empty())
    val storyData: StateFlow<PagingData<StoryItem>> = _storyData
    private val _storyLoading = MutableStateFlow(false)
    val storyLoading: StateFlow<Boolean> = _storyLoading
    private val _storyMessage = Channel<UiText>()
    val storyMessage = _storyMessage.receiveAsFlow()

    val stories = storyUseCases.getPagingStoryUseCase().cachedIn(viewModelScope)
}