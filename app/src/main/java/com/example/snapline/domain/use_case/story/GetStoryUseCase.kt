package com.example.snapline.domain.use_case.story

import com.example.snapline.data.remote.response.GetStoryResponse
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
import kotlinx.coroutines.runBlocking

class GetStoryUseCase(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<Resource<GetStoryResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = runBlocking { authRepository.getToken().first() }
            val response = storyRepository.getStoryWithLocation(Helper.formatToken(token))
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message ?: e.toString())))
        }
    }
        .flowOn(Dispatchers.IO)
}