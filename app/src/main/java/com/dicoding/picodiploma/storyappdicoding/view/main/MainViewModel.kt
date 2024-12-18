package com.dicoding.picodiploma.storyappdicoding.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserModel
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, storyRepository: StoryRepository) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    val story: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories()
//    val story: LiveData<PagingData<ListStoryItem>> = storyRepository.getStories()
//        .cachedIn(viewModelScope)
//        .asLiveData()

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}
