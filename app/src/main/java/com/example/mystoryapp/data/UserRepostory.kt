package com.example.mystoryapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.database.StoryRemoteMediator
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.response.LoginResult
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserRepository (
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: LoginResult) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<LoginResult> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        getSession().asLiveData()
        val user = runBlocking { getSession().first() }
        val apiService= ApiConfig.getApiService(user.token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(storyDatabase, userPreference, apiService)
            }.also { instance = it }
    }
}