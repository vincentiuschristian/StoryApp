package com.example.storyapp.data.paging.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_keys")
data class RemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)