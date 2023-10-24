package com.example.storyapp

import com.example.storyapp.data.paging.database.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val quote = StoryEntity(
                id = "id $i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://avatars.githubusercontent.com/u/95396799?v=4",
                createdAt = "2023-10-15T05:05:05Z",
                lat = i.toFloat(),
                lon = i.toFloat()
            )
            items.add(quote)
        }
        return items
    }
}
