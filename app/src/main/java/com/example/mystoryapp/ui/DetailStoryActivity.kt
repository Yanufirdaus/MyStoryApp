package com.example.mystoryapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.WelcomeActivity
import com.example.mystoryapp.databinding.ActivityDetailStoryBinding
import com.example.mystoryapp.ui.viewmodel.DetailStoryViewModel
import com.example.mystoryapp.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val detailStoryViewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var _activityDetailStoryBinding: ActivityDetailStoryBinding? = null
    private val binding get() = _activityDetailStoryBinding


    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.getSession().observe(this) { user ->
            if (user.token == "") {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token

                setupView()

                val idStory = intent.getStringExtra("idStory")

                detailStoryViewModel.getDetailStory(token, idStory!!)

                detailStoryViewModel.detailStory.observe(this){storyDetail ->
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val date = inputFormat.parse("${storyDetail.createdAt}")
                    val finalDate = outputFormat.format(date)

                    binding?.tvDetailName?.text = storyDetail.name
                    binding?.tvDetailDescription?.text = storyDetail.description
                    binding?.tvDateUpload?.text = finalDate
                    binding?.ivDetailPhoto?.let {
                        Glide.with(it.context)
                            .load(storyDetail.photoUrl)
                            .into(binding!!.ivDetailPhoto)
                    }
                }

                detailStoryViewModel.isLoading.observe(this){
                    showLoading(it)
                }
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
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility  = if (isLoading) View.VISIBLE else View.GONE
    }
}