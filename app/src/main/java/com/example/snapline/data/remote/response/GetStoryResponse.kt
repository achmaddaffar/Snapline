package com.example.snapline.data.remote.response

import android.os.Parcelable
import com.example.snapline.data.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetStoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>?,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("id")
    val id: String? = null,
) : Parcelable {

    fun toStoryEntity() = StoryEntity(
        photoUrl = this.photoUrl.toString(),
        createdAt = this.createdAt.toString(),
        name = this.name.toString(),
        description = this.description.toString(),
        lon = this.lon,
        lat = this.lat,
        id = this.id.toString(),
    )
}