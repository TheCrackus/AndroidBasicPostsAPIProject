package com.example.androidbasicpostsapiproject.data.remote.datasource

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.core.network.safeApiCall
import com.example.androidbasicpostsapiproject.data.remote.api.PostsApiService
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto

class PostRemoteDataSourceImpl(
    private val apiService: PostsApiService
) : PostRemoteDataSource {

    override suspend fun getPosts(): NetworkResult<List<PostDto>> {
        return safeApiCall { apiService.getPosts() }
    }
}
