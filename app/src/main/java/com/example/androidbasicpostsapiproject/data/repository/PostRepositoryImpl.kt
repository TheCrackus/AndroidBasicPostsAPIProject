package com.example.androidbasicpostsapiproject.data.repository

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.data.mapper.toDomain
import com.example.androidbasicpostsapiproject.data.remote.datasource.PostRemoteDataSource
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PostRepositoryImpl(
    private val remoteDataSource: PostRemoteDataSource
) : PostRepository {

    override fun getPosts(): Flow<NetworkResult<List<Post>>> = flow {
        emit(value = NetworkResult.Loading)

        when (val result = remoteDataSource.getPosts()) {
            is NetworkResult.Success -> emit(
                value = NetworkResult.Success(
                    data = result.data.toDomain()
                )
            )

            is NetworkResult.Error -> emit(
                value = result
            )

            is NetworkResult.Loading -> Unit
        }
    }.flowOn(context = Dispatchers.IO)
}
