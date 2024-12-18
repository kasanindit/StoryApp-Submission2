package com.dicoding.picodiploma.storyappdicoding.data.repository

import com.dicoding.picodiploma.storyappdicoding.data.api.ApiService
import com.dicoding.picodiploma.storyappdicoding.data.response.LoginResponse
import com.dicoding.picodiploma.storyappdicoding.data.response.RegisterResponse
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserModel
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(
            apiService: ApiService, userPreference: UserPreference
        ): UserRepository = UserRepository(userPreference, apiService)
    }
}