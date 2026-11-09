package com.example.androidbasicpostsapiproject.data.remote.datasource

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto

interface PostRemoteDataSource {
    suspend fun getPosts(): NetworkResult<List<PostDto>>
}
