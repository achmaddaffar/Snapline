package com.example.snapline.data.remote.network

import com.example.snapline.BuildConfig
import com.example.snapline.data.remote.response.GetStoryResponse
import com.example.snapline.data.remote.response.LoginResponse
import com.example.snapline.data.remote.response.RegisterResponse
import com.example.snapline.data.remote.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float? = null,
        @Part("lon") lon: Float? = null,
    ): Response<UploadStoryResponse>

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
    ): Response<GetStoryResponse>

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        @Volatile
        private var INSTANCE: ApiService? = null

        @JvmStatic
        fun getInstance(): ApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val loggingInterceptor =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor.Level.BODY)
                        else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                    val client = OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build()
                    Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build()
                        .create(ApiService::class.java)
                }
            }
        }
    }
}