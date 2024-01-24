package com.example.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.response.LoginResult
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<LoginResult> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStory().cachedIn(viewModelScope)
}