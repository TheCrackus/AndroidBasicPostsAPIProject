package com.example.androidbasicpostsapiproject.data.remote.api

import com.example.androidbasicpostsapiproject.core.constants.CREATE_POST_API_ENDPOINT
import com.example.androidbasicpostsapiproject.core.constants.POST_API_ENDPOINT
import com.example.androidbasicpostsapiproject.data.remote.model.CreatePostRequestDto
import com.example.androidbasicpostsapiproject.data.remote.model.CreatePostResponseDto
import com.example.androidbasicpostsapiproject.data.remote.model.PostsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PostsApiService {
    @GET(POST_API_ENDPOINT)
    suspend fun getPosts(): Response<PostsResponseDto>

    @POST(CREATE_POST_API_ENDPOINT)
    suspend fun createPost(
        @Body request: CreatePostRequestDto
    ): Response<CreatePostResponseDto>
}
