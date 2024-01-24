package com.example.mystoryapp.auth.login

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
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mystoryapp.Main.MainActivity
import com.example.mystoryapp.databinding.ActivityLoginBinding
import com.example.mystoryapp.R
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    private var error = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupView()
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupAction() {
        binding.edLoginEmail.hint = getString(R.string.email_hint)
        binding.edLoginPassword.hint = getString(R.string.password_hint)

        binding.edLoginButton.setOnClickListener {

            if (binding.edLoginEmail.text?.length  == 0){
                binding.edLoginEmail.setError(getString(R.string.null_email_allert), null)
            }
            if (binding.edLoginPassword.text?.length == 0){
                binding.edLoginPassword.setError(getString(R.string.null_password_allert), null)
            } else{
                loginViewModel.postLogin(binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString())
                loginViewModel.isLogin.observe(this@LoginActivity){loginStatus ->
                    if(loginStatus == true){
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        loginViewModel.errorLiveData.observe(this@LoginActivity) { errorMessage->
                            error = errorMessage
                            if (error != ""){
                                error = errorMessage
                                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                                error = ""
                            } else if (error == ""){
                                showLoading(true)
                            }
                        }
                    }
                }
                loginViewModel.isLoading.observe(this@LoginActivity) { isLoading ->
                    showLoading(isLoading)
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 3f).setDuration(2000)
        val tv3 = ObjectAnimator.ofFloat(binding.textView3, View.ALPHA, 3f).setDuration(500)
        val tv4 = ObjectAnimator.ofFloat(binding.textView4, View.ALPHA, 3f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.textFieldName, View.ALPHA, 3f).setDuration(500)
        val tv7 = ObjectAnimator.ofFloat(binding.textView7, View.ALPHA, 3f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.textFieldName2, View.ALPHA, 3f).setDuration(500)
        val btnlogin = ObjectAnimator.ofFloat(binding.edLoginButton, View.ALPHA, 3f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tv4, email, tv7, pass)
        }

        AnimatorSet().apply {
            playSequentially(title, tv3, together, btnlogin)
            start()
        }
    }
}