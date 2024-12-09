package com.example.snapline.domain.use_case.story

import android.net.Uri
import com.example.snapline.data.remote.response.UploadStoryResponse
import com.example.snapline.domain.repository.AuthRepository
import com.example.snapline.domain.repository.StoryRepository
import com.example.snapline.util.Helper
import com.example.snapline.util.Resource
import com.example.snapline.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UploadStoryUseCase(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository,
) {

    operator fun invoke(
        uri: Uri?,
        description: String,
        latitude: Float?,
        longitude: Float?
    ): Flow<Resource<UploadStoryResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = authRepository.getToken().first()
            val response = storyRepository.uploadStory(
                token = Helper.formatToken(token),
                uri = uri ?: Uri.EMPTY,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message ?: e.toString())))
        }
    }
        .flowOn(Dispatchers.IO)
}