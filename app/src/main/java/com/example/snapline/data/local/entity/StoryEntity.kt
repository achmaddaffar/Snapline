package com.example.snapline.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.snapline.domain.model.StoryItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    val photoUrl: String,
    val createdAt: String,
    val name: String,
    val description: String,
    val lon: Double? = null,
    val lat: Double? = null,
    @PrimaryKey
    val id: String,
) : Parcelable {
    fun toStoryItem() = StoryItem(
        photoUrl = this.photoUrl,
        createdAt = this.createdAt,
        name = this.name,
        description = this.description,
        lon = this.lon,
        lat = this.lat,
        id = this.id,
    )
}