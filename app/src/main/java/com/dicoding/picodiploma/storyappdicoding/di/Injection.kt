package com.dicoding.picodiploma.storyappdicoding.di

import android.content.Context
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.api.ApiConfig
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserPreference
import com.dicoding.picodiploma.storyappdicoding.data.pref.dataStore
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository

object Injection {
   fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getUserApiService(context)
        return UserRepository.getInstance(apiService, pref)
    }

   fun provideStoryRepository(context: Context): StoryRepository {
        val storyApiService = ApiConfig.getStoryApiService(context)
        return StoryRepository.getInstance(storyApiService)
    }
}





