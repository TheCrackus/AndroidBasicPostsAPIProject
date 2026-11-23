package com.example.androidbasicpostsapiproject.data.remote.datasource

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.data.remote.model.CreatePostRequestDto
import com.example.androidbasicpostsapiproject.data.remote.model.CreatePostResponseDto
import com.example.androidbasicpostsapiproject.data.remote.model.PostsResponseDto

interface PostRemoteDataSource {
    suspend fun getPosts(): NetworkResult<PostsResponseDto>

    suspend fun createPost(
        request: CreatePostRequestDto
    ): NetworkResult<CreatePostResponseDto>
}
