package com.dicoding.picodiploma.storyappdicoding.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyappdicoding.data.repository.StoryRepository
import com.dicoding.picodiploma.storyappdicoding.data.repository.UserRepository
import com.dicoding.picodiploma.storyappdicoding.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Result<AddStoryResponse>>()
    val uploadResult: LiveData<Result<AddStoryResponse>> = _uploadResult

    fun uploadStory(description: String, imageFile: File) {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStory(multipartBody, requestBody)
                _uploadResult.value = Result.success(response)
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            }
        }
    }
}