package com.example.storyapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Float,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Float
): Parcelable
