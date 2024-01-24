package com.example.mystoryapp.auth.signup

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
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.auth.login.LoginActivity
import com.example.mystoryapp.databinding.ActivitySignupBinding
import com.example.mystoryapp.ui.viewmodel.RegisterViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    var error = ""

    @SuppressLint("MissingInflatedId", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setAction()
        playAnimation()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        var tv = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 3f).setDuration(2000)
        var tv8 = ObjectAnimator.ofFloat(binding.textView8, View.ALPHA, 3f).setDuration(500)
        var tv11 = ObjectAnimator.ofFloat(binding.textView11, View.ALPHA, 3f).setDuration(500)
        var tfname = ObjectAnimator.ofFloat(binding.textFieldName, View.ALPHA, 3f).setDuration(500)
        var tv12 = ObjectAnimator.ofFloat(binding.textView12, View.ALPHA, 3f).setDuration(500)
        var tfname2 = ObjectAnimator.ofFloat(binding.textFieldName2, View.ALPHA, 3f).setDuration(500)
        var tv15 = ObjectAnimator.ofFloat(binding.textView15, View.ALPHA, 3f).setDuration(500)
        var tfname3 = ObjectAnimator.ofFloat(binding.textFieldName3, View.ALPHA, 3f).setDuration(500)
        var edregbtn = ObjectAnimator.ofFloat(binding.edRegisterButton, View.ALPHA, 3f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tv11, tfname, tv12, tfname2, tv15, tfname3)
        }

        AnimatorSet().apply {
            playSequentially(tv, tv8, together, edregbtn)
            start()
        }
    }

    private fun setView() {
        Suppress("DEPRECATION")
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
    private fun setAction() {

        binding.edRegisterName.hint = getString(R.string.name_hint)
        binding.edRegisterEmail.hint = getString(R.string.email_hint)
        binding.edRegisterPassword.hint = getString(R.string.password_hint)

        val registerViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegisterViewModel::class.java)

        binding.edRegisterButton.setOnClickListener {
            if (binding.edRegisterEmail.text?.length  == 0){
                binding.edRegisterEmail.setError(getString(R.string.null_email_allert), null)
            }
            if (binding.edRegisterName.text?.length == 0){
                binding.edRegisterName.setError(getString(R.string.null_name_allert), null)
            }
            if (binding.edRegisterPassword.text?.length == 0){
                binding.edRegisterPassword.setError(getString(R.string.null_password_allert), null)
            } else{
                registerViewModel.postRegister(binding.edRegisterName.text.toString(), binding.edRegisterEmail.text.toString(), binding.edRegisterPassword.text.toString())
                registerViewModel.isSignedup.observe(this@SignupActivity) {signupStatus ->
                    if(signupStatus == true) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, "signup berhasil", Toast.LENGTH_SHORT).show()
                    } else {
                        registerViewModel.errorLiveData.observe(this@SignupActivity) { errorMessage->
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
                registerViewModel.isLoading.observe(this@SignupActivity) { isLoading ->
                    showLoading(isLoading)
                }
            }
        }
    }
}