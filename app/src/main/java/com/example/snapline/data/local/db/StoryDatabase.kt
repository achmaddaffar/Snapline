package com.example.snapline.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.snapline.data.local.dao.RemoteKeyDao
import com.example.snapline.data.local.dao.StoryDao
import com.example.snapline.data.local.entity.RemoteKey
import com.example.snapline.data.local.entity.StoryEntity
import com.example.snapline.util.Constants

@Database(
    version = 1,
    entities = [
        StoryEntity::class,
        RemoteKey::class
    ]
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao

    abstract fun keyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getInstance(
            context: Context
        ): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    Room.databaseBuilder(
                        context = context,
                        klass = StoryDatabase::class.java,
                        name = Constants.STORY_DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
    }
}