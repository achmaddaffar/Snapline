package com.example.snapline.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.snapline.data.local.db.StoryDatabase
import com.example.snapline.data.local.entity.StoryEntity
import com.example.snapline.data.remote.network.ApiService
import com.example.snapline.data.remote.response.GetStoryResponse
import com.example.snapline.data.remote.response.UploadStoryResponse
import com.example.snapline.data.remote_mediator.StoryRemoteMediator
import com.example.snapline.data.util.GlobalErrorParser
import com.example.snapline.domain.repository.StoryRepository
import com.example.snapline.util.Constants
import com.example.snapline.util.Helper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val db: StoryDatabase,
    private val apiService: ApiService,
    private val errorParser: GlobalErrorParser,
    private val context: Context
) : StoryRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getStory(): LiveData<PagingData<StoryEntity>> {
        val token = runBlocking {
            dataStore.data.map { preferences ->
                preferences[Constants.TOKEN_KEY]
            }.first()
        }
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(
                apiService = apiService,
                db = db,
                token = Helper.formatToken(token)
            ),
            pagingSourceFactory = {
                db.storyDao().getAllStory()
            }
        ).liveData
    }

    override suspend fun getStoryWithLocation(token: String): GetStoryResponse {
        val response = apiService.getAllStory(
            token = token,
            page = 1,
            size = 15,
            location = 1
        )
        if (response.isSuccessful) {
            response.body()?.let { data -> return data }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun uploadStory(
        token: String,
        uri: Uri,
        description: String,
        latitude: Float?,
        longitude: Float?,
    ): UploadStoryResponse {
        val file = Helper.uriToFile(uri, context)
        val reducedFile = Helper.reduceFileImage(file)
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            reducedFile.name,
            requestImageFile
        )

        val response = apiService.uploadStory(
            token = token,
            file = imageMultipart,
            description = descriptionRequestBody,
            lat = latitude,
            lon = longitude
        )

        if (response.isSuccessful) {
            response.body()?.let { data -> return data }
        }

        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        @JvmStatic
        fun getInstance(
            dataStore: DataStore<Preferences>,
            db: StoryDatabase,
            apiService: ApiService,
            errorParser: GlobalErrorParser,
            context: Context
        ): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepositoryImpl(
                    dataStore = dataStore,
                    db = db,
                    apiService = apiService,
                    errorParser = errorParser,
                    context = context
                )
            }
        }
    }
}