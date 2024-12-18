package com.dicoding.picodiploma.storyappdicoding.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel (private val storyRepository: StoryRepository) : ViewModel() {

    private val _location = MutableLiveData<List<ListStoryItem>>()
    val location: LiveData<List<ListStoryItem>> get() = _location

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error


    fun fetchLocationStories() {
        viewModelScope.launch {
            try {
                val response = storyRepository.getLocationStory()
                Log.d("MapsViewModel", "Response: ${response.listStory}")
                _location.value = response.listStory.filter {
                    it.lat != null && it.lon != null
                }
            } catch (e: Exception) {
                _error.value = "Error fetching location: ${e.message}"
            }
        }
    }
}