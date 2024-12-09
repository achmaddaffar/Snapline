package com.example.snapline.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.snapline.domain.use_case.auth.AuthUseCases
import com.example.snapline.domain.use_case.creator.AuthUseCasesCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        apiService: ApiService,
//        dataStore: DataStore<Preferences>,
//        errorParser: GlobalErrorParser
//    ): AuthRepository = AuthRepositoryImpl(apiService, dataStore, errorParser)

    @Provides
    @Singleton
    fun provideAuthUseCases(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>
    ): AuthUseCases =
        AuthUseCasesCreator().create(context, dataStore) as AuthUseCases
}