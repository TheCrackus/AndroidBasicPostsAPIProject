package com.example.androidbasicpostsapiproject.data.remote.model

import com.example.androidbasicpostsapiproject.core.constants.POST_CONTENT_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_CREATED_AT_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_CREATOR_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_ID_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_IMAGE_URL_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_TITLE_DTO
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName(value = POST_ID_DTO) val _id: String?,
    @SerializedName(value = POST_TITLE_DTO) val title: String?,
    @SerializedName(value = POST_CONTENT_DTO) val content: String?,
    @SerializedName(value = POST_IMAGE_URL_DTO) val imageUrl: String?,
    @SerializedName(value = POST_CREATOR_DTO) val creator: String?,
    @SerializedName(value = POST_CREATED_AT_DTO) val createdAt: String?
)
