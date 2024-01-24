package com.example.mystoryapp.ui.viewmodel

import android.content.ContentValues
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.LoginResult
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private var _errorLiveData = MutableLiveData<String>()
    var errorLiveData: LiveData<String> = _errorLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    fun postLogin(name: String, email: String) {
        _errorLiveData.value = ""
        _isLogin.value = false
        _isLoading.value = true
        val client = ApiConfig.getApiService("").login(name, email)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    var name = response.body()?.loginResult?.name
                    var uid = response.body()?.loginResult?.userId
                    var token = response.body()?.loginResult?.token
                    fun saveSession(user: LoginResult) {
                        viewModelScope.launch {
                            repository.saveSession(user)
                        }
                    }
                    saveSession(LoginResult(uid!!, name!!, token!!))
                    _isLogin.value = true
                    _isLoading.value = false
                    Log.d(ContentValues.TAG, "onSuccess: ${response.body()?.loginResult?.name} ${response.body()?.loginResult?.token} ${response.body()?.loginResult?.userId}")
                } else {

                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val jsonInString = errorBody.string()
                        val errorResponse = Gson().fromJson(jsonInString, LoginResponse::class.java)
                        val errorMessage = errorResponse.message
                        _errorLiveData.value = errorMessage
                    } else {
                        _errorLiveData.value = "Error response body is null"
                    }
                    _isLogin.value = false
                    _isLoading.value = false
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message?.get(1)}")
            }
        })
    }
}