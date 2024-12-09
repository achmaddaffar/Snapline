package com.example.snapline.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.snapline.domain.use_case.creator.StoryUseCasesCreator
import com.example.snapline.domain.use_case.story.StoryUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoryModule {

//    @Singleton
//    @Provides
//    fun provideStoryRepository(
//        dataStore: DataStore<Preferences>,
//        apiService: ApiService,
//        errorParser: GlobalErrorParser,
//        applicationContext: Application,
//        db: StoryDatabase
//    ): StoryRepository = StoryRepositoryImpl.getInstance(
//        dataStore = dataStore,
//        db = db,
//        apiService = apiService,
//        errorParser = errorParser,
//        application = applicationContext
//    )

    @Singleton
    @Provides
    fun provideStoryUseCase(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): StoryUseCases = StoryUseCasesCreator().create(context, dataStore) as StoryUseCases

//    @Singleton
//    @Provides
//    fun provideStoryDatabase(
//        @ApplicationContext context: Context
//    ): StoryDatabase = Room.databaseBuilder(
//        context = context,
//        klass = StoryDatabase::class.java,
//        name = Constants.STORY_DATABASE_NAME
//    )
//        .fallbackToDestructiveMigration()
//        .build()
}