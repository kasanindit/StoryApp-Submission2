package com.dicoding.picodiploma.storyappdicoding.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storyappdicoding.R
import com.dicoding.picodiploma.storyappdicoding.adapter.StoryAdapter
import com.dicoding.picodiploma.storyappdicoding.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyappdicoding.view.ViewModelFactory
import com.dicoding.picodiploma.storyappdicoding.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.storyappdicoding.view.maps.MapsActivity
import com.dicoding.picodiploma.storyappdicoding.view.welcome.WelcomeActivity


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObserver()
        setupView()

        binding.fbAdd.setOnClickListener {
            showLoading(true)
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObserver() {
        viewModel.getSession().observe(this) { user ->
            showLoading(true)
            if (!user.isLogin) {
                showLoading(false)
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getData()
                showLoading(false)
                Log.d("MainActivity", "Token retrieved: ${user.token}")
            }
        }
        
    }

    private fun getData(){
        val storyAdapter = StoryAdapter()
        binding.rvItemStory.adapter = storyAdapter
        viewModel.story.observe(this){
            Log.d("PagingLog", "New PagingData received. Submitting to adapter.")
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        storyAdapter = StoryAdapter()
        binding.rvItemStory.layoutManager = LinearLayoutManager(this)
        binding.rvItemStory.adapter = storyAdapter

        showLoading(false)

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_logout -> {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.logout()
                binding.progressBar.visibility = View.GONE
                true
            }
            R.id.action_location -> {
                binding.progressBar.visibility = View.VISIBLE
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                binding.progressBar.visibility = View.GONE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}


