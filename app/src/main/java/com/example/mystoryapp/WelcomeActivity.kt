package com.example.mystoryapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.example.mystoryapp.auth.login.LoginActivity
import com.example.mystoryapp.auth.signup.SignupActivity
import com.example.mystoryapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var loginText: TextView
    private lateinit var signupText: TextView

    private lateinit var binding : ActivityWelcomeBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        setupAction()
        playAnimation()
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

    private  fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(1500)
        val logo = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(150)
        val textView2 = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(150)
        val textView18 = ObjectAnimator.ofFloat(binding.textView18, View.ALPHA, 1f).setDuration(150)
        val textView16 = ObjectAnimator.ofFloat(binding.textView16, View.ALPHA, 1f).setDuration(150)
        val loginText = ObjectAnimator.ofFloat(binding.loginBtnText, View.ALPHA, 1f).setDuration(150)
        val signupText = ObjectAnimator.ofFloat(binding.signupBtnText, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(title, logo, textView16, loginText, textView2, textView18, signupText)
            start()
        }
    }

    private fun setupAction() {
        loginText = findViewById(R.id.login_btn_text)
        signupText = findViewById(R.id.signup_btn_text)

        signupText.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
        loginText.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}