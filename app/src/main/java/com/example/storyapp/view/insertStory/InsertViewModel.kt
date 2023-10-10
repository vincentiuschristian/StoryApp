package com.example.storyapp.view.insertStory

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.UserRepository
import java.io.File

class InsertViewModel (private val repository: UserRepository): ViewModel(){
    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}