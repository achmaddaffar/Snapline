package com.example.snapline.data

import com.example.snapline.data.local.entity.StoryEntity
import com.example.snapline.domain.model.StoryItem

object DataDummy {

    fun generateStoryEntityList(): List<StoryEntity> {
        val items = arrayListOf<StoryEntity>()
        for (i in 0 .. 100) {
            val storyEntity = StoryEntity(
                photoUrl = "https://w7.pngwing.com/pngs/798/436/png-transparent-computer-icons-user-profile-avatar-profile-heroes-black-profile-thumbnail.png",
                createdAt = "created at $i",
                name = "user $i",
                description = "description $i",
                lon = i.toDouble(),
                id = "$i",
                lat = i.toDouble()
            )
            items.add(storyEntity)
        }
        return items
    }
}