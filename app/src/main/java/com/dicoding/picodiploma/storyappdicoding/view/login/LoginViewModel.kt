package com.dicoding.picodiploma.storyappdicoding.view.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.LoginResponse
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    val loginResult = MutableLiveData<Result<LoginResponse>>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                val login = response.loginResult
                if (login != null) {
                    if (response.error == false && login.token != null) {
                        val user = UserModel(
                            email = email,
                            token = login.token
                        )
                        repository.saveSession(user)
                    }
                }
                loginResult.postValue(Result.success(response))
                Log.d("LoginActivity", "Login Response: ${response.loginResult?.token}")
            } catch (e: Exception) {
                loginResult.postValue(Result.failure(e))
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}