package com.example.mystoryapp.ui.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.response.ListStoryItems
import com.example.mystoryapp.data.response.StoryResponse
import com.example.mystoryapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationViewModel(repository: UserRepository) : ViewModel(){

    private val _listStoryLocation = MutableLiveData<List<ListStoryItems>>()
    val listStoryLocation: LiveData<List<ListStoryItems>> = _listStoryLocation

    fun getStoryLocation(token: String){
        val client = ApiConfig.getApiService(token).getStoriesWithLocation()
        client.enqueue(object: Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if(response.isSuccessful){
                    Log.d(ContentValues.TAG, "onSuccess: ${response.message()}")
                    val story = response.body()
                    if (story != null) {
                        _listStoryLocation.value = story.listStory as List<ListStoryItems>?
                    }
                } else {
                    Log.d(ContentValues.TAG, "onSuccess: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}