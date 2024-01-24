package com.example.mystoryapp.Main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.WelcomeActivity
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.ui.LocationActivity
import com.example.mystoryapp.ui.PostActivity
import com.example.mystoryapp.ui.viewmodel.LoadingStateAdapter
import com.example.mystoryapp.ui.viewmodel.MainViewModel
import com.example.mystoryapp.ui.viewmodel.StoryAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupView()
        playAnimation()
    }

    private fun setupAction() {
        viewModel.getSession().observe(this) { user ->
            if (user.token == "") {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token

                binding.postDirection.setOnClickListener{
                    val intent = Intent(binding.root.context, PostActivity::class.java)
                    intent.putExtra("token", token)
                    binding.root.context.startActivity(intent)
                }

                binding.textTry.text = user.name.uppercase(Locale.getDefault())

                binding.actionLogout.setOnClickListener{
                    viewModel.logout()
                }

                val layoutManager = LinearLayoutManager(this)
                binding.rvStory.layoutManager = layoutManager
                val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
                binding.rvStory.addItemDecoration(itemDecoration)

                getData()

                binding.cvLocationButton.setOnClickListener{
                    val intent = Intent(binding.root.context, LocationActivity::class.java)
                    intent.putExtra("token", token)
                    binding.root.context.startActivity(intent)
                }
            }
            getData()
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

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(this) {pagingData ->
            if (pagingData != null){
                adapter.submitData(lifecycle, pagingData)
            }
        }
    }

    private fun playAnimation() {
        val tv13 = ObjectAnimator.ofFloat(binding.textView13, View.ALPHA, 3f).setDuration(2000)
        val tvTry = ObjectAnimator.ofFloat(binding.textTry, View.ALPHA, 3f).setDuration(500)
        val postDir = ObjectAnimator.ofFloat(binding.postDirection, View.ALPHA, 3f).setDuration(500)
        val rv = ObjectAnimator.ofFloat(binding.rvStory, View.ALPHA, 3f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tv13, tvTry, postDir, rv)
            start()
        }
    }
}