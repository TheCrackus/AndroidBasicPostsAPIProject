package com.example.androidbasicpostsapiproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName(value = "_id") val _id: String?,
    @SerializedName(value = "title") val title: String?,
    @SerializedName(value = "content") val content: String?,
    @SerializedName(value = "imageUrl") val imageUrl: String?,
    @SerializedName(value = "creator") val creator: String?,
    @SerializedName(value = "createdAt") val createdAt: String?
)
