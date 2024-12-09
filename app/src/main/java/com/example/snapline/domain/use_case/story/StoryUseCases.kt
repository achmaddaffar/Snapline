package com.example.snapline.domain.use_case.story

import com.example.snapline.domain.use_case.creator.BaseUseCases

data class StoryUseCases(
    val uploadStoryUseCase: UploadStoryUseCase,
    val getStoryUseCase: GetStoryUseCase,
    val getPagingStoryUseCase: GetPagingStoryUseCase
): BaseUseCases