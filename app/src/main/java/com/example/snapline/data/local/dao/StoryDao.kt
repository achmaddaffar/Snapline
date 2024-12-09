package com.example.snapline.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snapline.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryList(storyList: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}