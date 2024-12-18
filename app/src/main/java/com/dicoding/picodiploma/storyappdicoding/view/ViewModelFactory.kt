package com.dicoding.picodiploma.storyappdicoding.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.di.Injection
import com.dicoding.picodiploma.storyappdicoding.view.addstory.AddStoryViewModel
import com.dicoding.picodiploma.storyappdicoding.view.login.LoginViewModel
import com.dicoding.picodiploma.storyappdicoding.view.main.MainViewModel
import com.dicoding.picodiploma.storyappdicoding.view.maps.MapsViewModel
import com.dicoding.picodiploma.storyappdicoding.view.signup.SignUpViewModel

class ViewModelFactory (private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(
            context: Context
        ): ViewModelFactory = ViewModelFactory(
            Injection.provideUserRepository(context),
            Injection.provideStoryRepository(context)
        )
    }
}
