package com.example.mystoryapp.ui.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.response.RegisterResponse
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSignedup = MutableLiveData<Boolean>()
    val isSignedup: LiveData<Boolean> = _isSignedup

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    fun postRegister(name: String, email: String, password: String) {
        _errorLiveData.value = ""
        _isSignedup.value = false
        _isLoading.value = true
        val client = ApiConfig.getApiService("").register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Log.d(TAG, "onSuccess: ${response.message()}")
                    _isSignedup.value = true
                    _isLoading.value = false
                } else {
                    Log.e(TAG, "onFailure: ${response.errorBody().toString()}")

                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val jsonInString = errorBody.string()
                        val errorResponse = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                        val errorMessage = errorResponse.message
                        _errorLiveData.value = errorMessage
                    } else {
                        _errorLiveData.value = "Error response body is null"
                    }
                    _isSignedup.value = false
                    _isLoading.value = false
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }
        })
    }
}