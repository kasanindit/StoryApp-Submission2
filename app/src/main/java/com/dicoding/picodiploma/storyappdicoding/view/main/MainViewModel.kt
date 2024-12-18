package com.dicoding.picodiploma.storyappdicoding.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem
import com.dicoding.picodiploma.storyappdicoding.data.pref.UserModel
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<PagingData<ListStoryItem>>()
    val stories: LiveData<PagingData<ListStoryItem>> get() = _stories

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchStories()
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                storyRepository.getStories().observeForever { pagingData ->
                    _stories.postValue(pagingData)
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}
