package com.example.snapline.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.snapline.data.local.ds.DataStoreConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

//    @Provides
//    @Singleton
//    fun provideApiService(): ApiService {
//        val loggingInterceptor =
//            if (BuildConfig.DEBUG) HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//            else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
//        val client = OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .build()
//        return Retrofit.Builder()
//            .baseUrl(ApiService.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//            .create(ApiService::class.java)
//    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        DataStoreConfig.getInstance(context)

//    @Singleton
//    @Provides
//    fun provideGson(): Gson = Gson()
//
//    @Singleton
//    @Provides
//    fun provideGlobalErrorParser(gson: Gson): GlobalErrorParser = GlobalErrorParser(gson)
}