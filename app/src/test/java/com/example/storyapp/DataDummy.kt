package com.example.storyapp

import com.example.storyapp.data.paging.database.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..25) {
            val quote = StoryEntity(
                i.toString(),
                "createdAt + $i",
                "description $i",
                "name $i",
                "photoUrl $i",
                0.0F,
                0.0F,
            )
            items.add(quote)
        }
        return items
    }
}
