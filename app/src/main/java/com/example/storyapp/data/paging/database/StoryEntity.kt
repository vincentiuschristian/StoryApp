package com.example.storyapp.data.paging.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryEntity(

    @PrimaryKey
    @field: ColumnInfo("id")
    val id: String,

    @field: ColumnInfo("name")
    val name: String,

    @field: ColumnInfo("photoUrl")
    val photoUrl: String,

    @field: ColumnInfo("description")
    val description: String,

    @field: ColumnInfo("createdAt")
    val createdAt: String,

    @field: ColumnInfo("lat")
    val lat: Float? = null,

    @field: ColumnInfo("lon")
    val lon: Float? = null

) : Parcelable