package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getAllStories() = repository.getStories()

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}