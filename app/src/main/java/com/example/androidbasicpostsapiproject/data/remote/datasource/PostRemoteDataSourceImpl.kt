package com.example.androidbasicpostsapiproject.data.remote.datasource

import com.example.androidbasicpostsapiproject.core.coroutines.DispatcherProvider
import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.core.network.safeApiCall
import com.example.androidbasicpostsapiproject.data.remote.api.PostsApiService
import com.example.androidbasicpostsapiproject.data.remote.model.PostsResponseDto
import kotlinx.serialization.json.Json

class PostRemoteDataSourceImpl(
    private val apiService: PostsApiService,
    private val dispatchers: DispatcherProvider,
    private val json: Json,
) : PostRemoteDataSource {

    override suspend fun getPosts(): NetworkResult<PostsResponseDto> {
        return safeApiCall(
            dispatcher = dispatchers.io,
            json = json,
        ) {
            apiService.getPosts()
        }
    }
}
