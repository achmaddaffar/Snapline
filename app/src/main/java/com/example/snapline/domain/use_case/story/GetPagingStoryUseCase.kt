package com.example.snapline.domain.use_case.story

import com.example.snapline.domain.repository.StoryRepository

class GetPagingStoryUseCase(
    private val storyRepository: StoryRepository
) {

    operator fun invoke() = storyRepository.getStory()
}