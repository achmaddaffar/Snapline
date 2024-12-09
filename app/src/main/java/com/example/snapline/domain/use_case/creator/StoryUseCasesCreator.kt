package com.example.snapline.domain.use_case.creator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.snapline.data.local.db.StoryDatabase
import com.example.snapline.data.remote.network.ApiService
import com.example.snapline.data.repository.AuthRepositoryImpl
import com.example.snapline.data.repository.StoryRepositoryImpl
import com.example.snapline.data.util.GlobalErrorParser
import com.example.snapline.domain.use_case.story.GetPagingStoryUseCase
import com.example.snapline.domain.use_case.story.GetStoryUseCase
import com.example.snapline.domain.use_case.story.StoryUseCases
import com.example.snapline.domain.use_case.story.UploadStoryUseCase
import com.google.gson.Gson

class StoryUseCasesCreator: BaseUseCasesCreator() {
    override fun create(
        context: Context,
        dataStore: DataStore<Preferences>
    ): BaseUseCases {
        val apiService = ApiService.getInstance()
        val errorParser = GlobalErrorParser(Gson())
        val db = StoryDatabase.getInstance(context.applicationContext)

        val storyRepository = StoryRepositoryImpl.getInstance(
            dataStore = dataStore,
            db = db,
            apiService = apiService,
            errorParser = errorParser,
            context = context
        )
        val authRepository = AuthRepositoryImpl.getInstance(
            apiService = apiService,
            dataStore = dataStore,
            errorParser = errorParser
        )

        return StoryUseCases(
            uploadStoryUseCase = UploadStoryUseCase(
                storyRepository = storyRepository,
                authRepository = authRepository
            ),
            getStoryUseCase = GetStoryUseCase(
                storyRepository = storyRepository,
                authRepository = authRepository
            ),
            getPagingStoryUseCase = GetPagingStoryUseCase(storyRepository)
        )
    }
}