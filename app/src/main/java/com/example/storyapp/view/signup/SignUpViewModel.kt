package com.example.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.UserRepository

class SignUpViewModel(private val userRepository: UserRepository): ViewModel() {

    fun register(name: String, email: String, pass: String) = userRepository.registerUser(name, email, pass)

}