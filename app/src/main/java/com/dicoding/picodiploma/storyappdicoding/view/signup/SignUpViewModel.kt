package com.dicoding.picodiploma.storyappdicoding.view.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.RegisterResponse
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {

    val signupResult = MutableLiveData<Result<RegisterResponse>>()

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                signupResult.postValue(Result.success(response))
            }catch (e: SocketTimeoutException) {
                signupResult.postValue(Result.failure(Exception("Request timeout. Silakan coba lagi.")))
            } catch (e: Exception) {
                signupResult.postValue(Result.failure(e))
            }
        }
    }


}