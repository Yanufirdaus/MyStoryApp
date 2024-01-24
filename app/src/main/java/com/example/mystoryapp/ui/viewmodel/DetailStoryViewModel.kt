package com.example.mystoryapp.ui.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.response.DetailStoryResponse
import com.example.mystoryapp.data.response.Story
import com.example.mystoryapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel(repository: UserRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<Story>()
    val detailStory: LiveData<Story> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailStory(token: String, storyId : String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).getDetailStory(storyId)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                if (response.isSuccessful) {
                    var response = response.body()?.story
                    _detailStory.value = response!!
                    _isLoading.value = false
                } else{
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }
}