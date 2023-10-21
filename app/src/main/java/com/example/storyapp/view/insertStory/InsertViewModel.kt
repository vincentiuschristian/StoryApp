package com.example.storyapp.view.insertStory

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.UserRepository
import java.io.File

class InsertViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadStory(file: File, description: String, lat: Float?, long: Float?) =
        repository.uploadStory(file, description, lat, long)
}