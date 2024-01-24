package com.example.mystoryapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: LoginResult) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId
            preferences[USER_NAME] = user.name
            preferences[USER_TOKEN] = user.token
        }
    }

    fun getStory(): Flow<ListStoryItem> {
        return dataStore.data.map { preferences ->
            ListStoryItem(
                preferences[PHOTO_URL] ?: "",
                preferences[CREATED_AT] ?: "",
                preferences[NAME] ?: "",
                preferences[DESCRIPTION] ?: "",
                preferences[LON]?: 0.0,
                preferences[STORY_ID] ?: "",
                preferences[LAT] ?: 0.0,
            )
        }
    }

    fun getSession(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                preferences[USER_ID] ?: "",
                preferences[USER_NAME] ?: "",
                preferences[USER_TOKEN] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID = stringPreferencesKey("userId")
        private val USER_NAME = stringPreferencesKey("name")
        private val USER_TOKEN = stringPreferencesKey("token")

        private val PHOTO_URL = stringPreferencesKey("photoUrl")
        private val CREATED_AT = stringPreferencesKey("createdAt")
        private val NAME = stringPreferencesKey("name")
        private val DESCRIPTION = stringPreferencesKey("description")
        private val LON = doublePreferencesKey("lon")
        private val STORY_ID = stringPreferencesKey("id")
        private val LAT = doublePreferencesKey("lat")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

