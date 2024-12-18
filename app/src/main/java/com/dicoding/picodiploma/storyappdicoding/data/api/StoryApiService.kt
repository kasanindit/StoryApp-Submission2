package com.dicoding.picodiploma.storyappdicoding.data.api

import com.dicoding.picodiploma.storyappdicoding.data.response.AddStoryResponse
import com.dicoding.picodiploma.storyappdicoding.data.response.DetailResponse
import com.dicoding.picodiploma.storyappdicoding.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApiService {
    @GET("stories")
    suspend fun getStory(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetail(
        @Path("id") id: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
//        @Part("lat") lat: RequestBody,
//        @Part("lon") lon: RequestBody,
    ): AddStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
            @Query("location") location : Int = 1,
    ): StoryResponse
}

