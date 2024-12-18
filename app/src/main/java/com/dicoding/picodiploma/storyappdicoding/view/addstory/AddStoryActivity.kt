package com.dicoding.picodiploma.storyappdicoding.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyappdicoding.R
import com.dicoding.picodiploma.storyappdicoding.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.storyappdicoding.di.Injection
import com.dicoding.picodiploma.storyappdicoding.getImageUri
import com.dicoding.picodiploma.storyappdicoding.reduceFileImage
import com.dicoding.picodiploma.storyappdicoding.uriToFile
import com.dicoding.picodiploma.storyappdicoding.view.ViewModelFactory
import com.dicoding.picodiploma.storyappdicoding.view.main.MainActivity
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null
    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val factory = ViewModelFactory(
            Injection.provideUserRepository(this),
            Injection.provideStoryRepository(this)
        )
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

        setupView()
        setupObserver()
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { handleUploadStory() }

    }

    private fun requestLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLon = location.longitude
            }
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
        supportActionBar?.hide()
    }

    private fun setupObserver() {
        addStoryViewModel.uploadResult.observe(this) { result ->
            showLoading(false)
            result.onSuccess { response ->
                showToast(response.message.toString())

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }.onFailure { error ->
                showToast(error.localizedMessage ?: getString(R.string.upload_failed))
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun handleUploadStory() {
        val isLocationEnabled = binding.switchLocation.isChecked
        if (isLocationEnabled) requestLocation()

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.etDescription.text.toString()

            if (description.isBlank()) {
                showToast(getString(R.string.empty_description_warning))
                return
            }

            showLoading(true)
            addStoryViewModel.uploadStory(
                description,
                imageFile,
                currentLat,
                currentLon
            )
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        runOnUiThread{ binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val LOCATION_REQUEST_CODE = 1
    }

}