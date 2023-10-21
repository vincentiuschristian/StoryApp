package com.example.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoriesWithLocation() = repository.getStoriesWithLocation()

}