package com.example.mystoryapp.di

import android.content.Context
import com.example.mystoryapp.data.*
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection{
    fun provideRepository(context: Context): UserRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(database, pref, apiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}