package com.example.androidbasicpostsapiproject.data.remote.model

import com.example.androidbasicpostsapiproject.core.constants.POSTS_RESPONSE_LIST_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_CONTENT_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_CREATED_AT_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_CREATOR_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_ID_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_IMAGE_URL_DTO
import com.example.androidbasicpostsapiproject.core.constants.POST_TITLE_DTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostsResponseDto(
    @SerialName(value = POSTS_RESPONSE_LIST_DTO) val posts: List<PostDto>? = null,
)

@Serializable
data class PostDto(
    @SerialName(value = POST_ID_DTO) val _id: String? = null,
    @SerialName(value = POST_TITLE_DTO) val title: String? = null,
    @SerialName(value = POST_CONTENT_DTO) val content: String? = null,
    @SerialName(value = POST_IMAGE_URL_DTO) val imageUrl: String? = null,
    @SerialName(value = POST_CREATOR_DTO) val creator: String? = null,
    @SerialName(value = POST_CREATED_AT_DTO) val createdAt: String? = null,
)
