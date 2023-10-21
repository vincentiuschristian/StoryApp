package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.paging.database.StoryDatabase
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return UserRepository.getInstance(pref, apiService, database)
    }
}