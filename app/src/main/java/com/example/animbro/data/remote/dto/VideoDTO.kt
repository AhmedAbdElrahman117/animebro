package com.example.animbro.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoDTO(
    val id: Int,
    val title: String,
    val url: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val thumbnail: String

)
