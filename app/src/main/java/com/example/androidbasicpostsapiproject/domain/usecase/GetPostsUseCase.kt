package com.example.androidbasicpostsapiproject.domain.usecase

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

/**
 * Domain use case that retrieves the list of [Post] items from [PostRepository].
 *
 * @param repository Source repository providing the posts feed stream.
 */
class GetPostsUseCase(
    private val repository: PostRepository
) {
    /** Executes the use case and returns a cold [Flow] of [NetworkResult] updates. */
    operator fun invoke(): Flow<NetworkResult<List<Post>>> = repository.getPosts()
}
