package com.example.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.view.welcome.WelcomeActivity

class AuthActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)



    }


}