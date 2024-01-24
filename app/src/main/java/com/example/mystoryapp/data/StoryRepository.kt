package com.example.mystoryapp.data

import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getStory(): Flow<ListStoryItem> {
        return userPreference.getStory()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
        }
    }
}
