package com.dicoding.picodiploma.storyappdicoding.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.storyappdicoding.data.api.StoryApiService
import com.dicoding.picodiploma.storyappdicoding.data.pref.StoryPreference
import com.dicoding.picodiploma.storyappdicoding.data.response.AddStoryResponse
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem
import com.dicoding.picodiploma.storyappdicoding.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val storyApiService: StoryApiService,
    private val storyPreference: StoryPreference
) {

    fun getStories(): LiveData<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                StoryPagingResources(storyApiService)
            }
        ).flow.asLiveData()
    }

    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        return storyApiService.uploadStory(file, description)
    }

    suspend fun getLocationStory(): StoryResponse {
        try {
            return storyApiService.getStoriesWithLocation()
        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories", e)
            throw e
        }
    }


    companion object {
        fun getInstance(
            apiService: StoryApiService, storyPreference: StoryPreference
        ): StoryRepository = StoryRepository(apiService, storyPreference)
    }
}