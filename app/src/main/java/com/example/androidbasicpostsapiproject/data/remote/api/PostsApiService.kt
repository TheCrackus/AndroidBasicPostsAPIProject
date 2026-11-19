package com.example.androidbasicpostsapiproject.data.remote.api

import com.example.androidbasicpostsapiproject.core.constants.POST_API_ENDPOINT
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto
import retrofit2.Response
import retrofit2.http.GET

interface PostsApiService {
    @GET(POST_API_ENDPOINT)
    suspend fun getPosts(): Response<List<PostDto>>
}
