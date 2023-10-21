package com.example.storyapp.view.insertStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.ResultState
import com.example.storyapp.databinding.ActivityInsertStoryBinding
import com.example.storyapp.util.getImageUri
import com.example.storyapp.util.reduceFileImage
import com.example.storyapp.util.uriToFile
import com.example.storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class InsertStoryActivity : AppCompatActivity() {
    private val binding: ActivityInsertStoryBinding by lazy {
        ActivityInsertStoryBinding.inflate(layoutInflater)
    }
    private var currentImageUri: Uri? = null
    private var location: Location? = null
    private var latitude: Float? = null
    private var longitude: Float? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val insertViewModel: InsertViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { uploadImage() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                getMyLastLocation()
            }
        }

        setupView()

        if (!checkPermission()){
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSIONS
            )
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

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
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
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edtDesc.text.toString()
            val lat = if(location != null) latitude.toString().toRequestBody("text/plain". toMediaType()) else null
            val lon = if(location != null) longitude.toString().toRequestBody("text/plain". toMediaType()) else null

            insertViewModel.uploadStory(imageFile, description, latitude, longitude).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showSnackbar(result.data.message.toString())
                            showLoading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        is ResultState.Error -> {
                            showSnackbar(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showSnackbar(resources.getString(R.string.insertPhotoWarning))
    }

    private val requestPermissionLauncherLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
            }
        }

    private fun checkPermission() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { locs: Location? ->
                if (locs != null) {
                    latitude = locs.latitude.toFloat()
                    longitude = locs.longitude.toFloat()
                    Log.d("InsertStory", "loc : ${location?.latitude}, ${location?.latitude}")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncherLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressIndicator.visibility = View.VISIBLE
                btnUpload.isEnabled = false
            } else {
                progressIndicator.visibility = View.GONE
                btnUpload.isEnabled = true
            }
        }
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message!!, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 200
    }
}