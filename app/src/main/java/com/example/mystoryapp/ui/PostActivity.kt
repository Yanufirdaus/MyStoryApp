package com.example.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mystoryapp.Main.MainActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.data.response.UploadResponse
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.databinding.ActivityPostBinding
import com.example.mystoryapp.getImageUri
import com.example.mystoryapp.reduceFileImage
import com.example.mystoryapp.uriToFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null

    var token = ""
    var description = ""
    var latitude = 0.0F
    var longitude = 0.0F

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
        setupAction()
        playAnimation()
        getMyLocation()
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

    private fun setupAction() {
        val tokenuser = intent.getStringExtra("token")
        token = tokenuser!!

        binding.imageUpload.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.image_option, null)
            bottomSheetDialog.setContentView(view)

            val btnCamera = view.findViewById<Button>(R.id.button_camera)
            val btnGallery = view.findViewById<Button>(R.id.button_gallery)

            bottomSheetDialog.show()

            btnCamera.setOnClickListener {
                startCamera()
            }

            btnGallery.setOnClickListener {
                startGallery()
            }
        }

        binding.buttonPost.setOnClickListener{
            uploadImage()
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
            binding.imageUpload.setImageURI(it)
        }
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                    }
                }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadImage() {
        if(binding.checkboxLocation.isChecked){
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                description = binding.editTextTextPersonName2.text.toString()

                showLoading(true)

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                lifecycleScope.launch {
                    try {
                        val apiService = ApiConfig.getApiService(token)
                        val response = apiService.uploadImageWithLocation(multipartBody, requestBody, latitude, longitude)
                        response.enqueue(object : Callback<UploadResponse> {
                            override fun onResponse(
                                call: Call<UploadResponse>,
                                response: Response<UploadResponse>
                            ) {
                                if (response.isSuccessful){
                                    showToast("Success")
                                    showLoading(false)
                                    startMainActivity()
                                } else {
                                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                showLoading(false)
                                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                            }
                        })

                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                        showToast(errorResponse.message!!)
                        showLoading(false)
                    }
                }
            } ?: showToast(getString(R.string.empty_image_warning))
        } else {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                description = binding.editTextTextPersonName2.text.toString()

                showLoading(true)

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                lifecycleScope.launch {
                    try {
                        val apiService = ApiConfig.getApiService(token)
                        val response = apiService.uploadImage(multipartBody, requestBody)
                        response.enqueue(object : Callback<UploadResponse> {
                            override fun onResponse(
                                call: Call<UploadResponse>,
                                response: Response<UploadResponse>
                            ) {
                                if (response.isSuccessful){
                                    showToast("Success")
                                    showLoading(false)
                                    startMainActivity()
                                } else {
                                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                showLoading(false)
                                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                            }
                        })

                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                        showToast(errorResponse.message!!)
                        showLoading(false)
                    }
                }
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun playAnimation() {
        val tv5 = ObjectAnimator.ofFloat(binding.textView5, View.TRANSLATION_Y, -60f, 0f).setDuration(1500)
        val cv2 = ObjectAnimator.ofFloat(binding.cardView2, View.TRANSLATION_Y, -60f, 0f).setDuration(1500)
        val tv6 = ObjectAnimator.ofFloat(binding.textView6, View.TRANSLATION_X, -60f, 0f).setDuration(1500)
        val cv3 = ObjectAnimator.ofFloat(binding.cardView3, View.TRANSLATION_Y, 60f, 0f).setDuration(1500)
        val btnPost = ObjectAnimator.ofFloat(binding.buttonPost, View.TRANSLATION_Y, 60f, 0f).setDuration(1500)

        AnimatorSet().apply {
            playTogether(tv5, cv2, tv6, cv3, btnPost)
            start()
        }
    }
}