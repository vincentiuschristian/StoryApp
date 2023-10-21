@file:OptIn(ExperimentalPagingApi::class)

package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.paging.database.StoryEntity
import com.example.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    val getAllStories: LiveData<PagingData<StoryEntity>> =
        repository.getStory().cachedIn(viewModelScope)


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}