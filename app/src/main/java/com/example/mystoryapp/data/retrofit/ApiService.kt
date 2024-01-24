package com.example.mystoryapp.data.retrofit

import com.example.mystoryapp.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(): Call<ListStoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(@Path("id") id: String): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @Multipart
    @POST("stories")
    fun uploadImageWithLocation(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): Call<UploadResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ListStoryResponse
}