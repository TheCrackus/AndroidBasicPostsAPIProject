package com.example.androidbasicpostsapiproject.data.repository

import com.example.androidbasicpostsapiproject.core.coroutines.DispatcherProvider
import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.data.mapper.toDomain
import com.example.androidbasicpostsapiproject.data.remote.datasource.PostRemoteDataSource
import com.example.androidbasicpostsapiproject.data.remote.model.CreatePostRequestDto
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Default [PostRepository] implementation backed by [PostRemoteDataSource].
 *
 * Emits [NetworkResult.Loading] before querying the remote source, maps DTO envelopes to pure
 * domain models via [toDomain], and executes upstream work on [DispatcherProvider.io].
 *
 * @param remoteDataSource Remote data source executing API requests.
 * @param dispatchers Injectable [DispatcherProvider] controlling upstream execution context.
 */
class PostRepositoryImpl(
    private val remoteDataSource: PostRemoteDataSource,
    private val dispatchers: DispatcherProvider
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
    }.flowOn(context = dispatchers.io)

    override fun createPost(
        title: String,
        content: String
    ): Flow<NetworkResult<Post>> = flow {
        emit(value = NetworkResult.Loading)

        val request = CreatePostRequestDto(
            title = title,
            content = content
        )

        when (val result = remoteDataSource.createPost(request = request)) {
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
    }.flowOn(context = dispatchers.io)
}
