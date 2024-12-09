package com.example.snapline.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.snapline.data.local.entity.StoryEntity
import com.example.snapline.data.remote.response.GetStoryResponse
import com.example.snapline.data.remote.response.UploadStoryResponse

interface StoryRepository {

    fun getStory(): LiveData<PagingData<StoryEntity>>

    suspend fun getStoryWithLocation(
        token: String
    ): GetStoryResponse

    suspend fun uploadStory(
        token: String,
        uri: Uri,
        description: String,
        latitude: Float? = null,
        longitude: Float? = null
    ): UploadStoryResponse
}