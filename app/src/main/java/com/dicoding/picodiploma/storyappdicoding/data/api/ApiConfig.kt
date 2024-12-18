package com.dicoding.picodiploma.storyappdicoding.data.api

import android.content.Context
import android.util.Log
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserPreference
import com.dicoding.picodiploma.storyappdicoding.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getUserApiService(context: Context): ApiService {
        return createApiService(context)
    }

    fun getStoryApiService(context: Context): StoryApiService {
        return createApiService(context)
    }

    private inline fun <reified T> createApiService(context: Context): T {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { pref.getSession().first().token }
        Log.d("ApiConfig", "Fetched Token: $token")

        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            Log.d("ApiConfig", "Request Headers: ${requestHeaders.header("Authorization")}")
            chain.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(T::class.java)
    }
}