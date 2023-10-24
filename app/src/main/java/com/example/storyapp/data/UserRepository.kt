package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.paging.data.StoryRemoteMediator
import com.example.storyapp.data.paging.database.StoryDatabase
import com.example.storyapp.data.paging.database.StoryEntity
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.response.ErrorResponse
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.response.StoryResponse
import com.example.storyapp.util.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            Log.d("registerUser", "Error: ${e.response()?.errorBody()}")
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(ResultState.Error(e.toString()))
        }
    }

    fun loginUser(email: String, password: String): LiveData<ResultState<LoginResponse>> =
        liveData {
            emit(ResultState.Loading)
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.login(email, password)
                    emit(ResultState.Success(response))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    emit(ResultState.Error(errorResponse.message.toString()))
                } catch (e: Exception) {
                    emit(ResultState.Error(e.toString()))
                }
            }
        }

    @ExperimentalPagingApi
    fun getStory(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun uploadStory(imageFile: File, description: String, lat: Float?, long: Float?) = liveData {
        emit(ResultState.Loading)
        val requestBodyDesc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val sessionToken = "Bearer ${userPreference.getSession().first().token}"
            val successResponse =
                apiService.uploadStory(sessionToken, multipartBody, requestBodyDesc, lat, long)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(ResultState.Error(e.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<ResultState<StoryResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val sessionToken = "Bearer ${userPreference.getSession().first().token}"
                val response = apiService.getStoriesWithLocation(sessionToken, 1)
                emit(ResultState.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                emit(ResultState.Error(errorResponse.message.toString()))
            } catch (e: Exception) {
                emit(ResultState.Error(e.toString()))
            }
        }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService, storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }
}