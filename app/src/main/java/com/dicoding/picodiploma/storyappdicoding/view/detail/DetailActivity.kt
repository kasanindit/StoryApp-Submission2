package com.dicoding.picodiploma.storyappdicoding.view.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.storyappdicoding.data.api.ApiConfig
import com.dicoding.picodiploma.storyappdicoding.data.api.StoryApiService
import com.dicoding.picodiploma.storyappdicoding.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var storyApiService: StoryApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyApiService = ApiConfig.getStoryApiService(this)

        val storyId = intent.getStringExtra(STORY_ID)
        Log.d("DetailActivity", "Received storyId: $storyId")

        if (storyId.isNullOrEmpty()) {
            Toast.makeText(this, "Story ID is invalid", Toast.LENGTH_SHORT).show()
        } else {
            getStoryDetail(storyId)
        }

        getStoryDetail(storyId)
    }

    private fun getStoryDetail(storyId: String?) {
        if (storyId != null) {
            lifecycleScope.launch {
                try {
                    val response = storyApiService.getDetail(storyId)
                    if (!response.error!!) {
                        val story = response.story
                        binding.txtOwnerName.text = story?.name
                        binding.txtDescription.text = story?.description
                        Glide.with(this@DetailActivity)
                            .load(story?.photoUrl)
                            .into(binding.ivStory)
                    } else {
                        Toast.makeText(this@DetailActivity, "Failed to load story", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }else {
            Toast.makeText(this, "Story ID is invalid", Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        const val STORY_ID = "story_id"
    }
}