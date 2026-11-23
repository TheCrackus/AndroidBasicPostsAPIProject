package com.example.androidbasicpostsapiproject.domain.repository

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract for reading and managing [Post] resources.
 */
interface PostRepository {
    /**
     * Streams the posts feed as a cold [Flow] emitting [NetworkResult.Loading] followed by either
     * [NetworkResult.Success] or [NetworkResult.Error].
     */
    fun getPosts(): Flow<NetworkResult<List<Post>>>

    /**
     * Submits a new post creation request as a cold [Flow] emitting [NetworkResult.Loading]
     * followed by either [NetworkResult.Success] with the created [Post] or [NetworkResult.Error].
     *
     * @param title Title of the post to create.
     * @param content Body content of the post to create.
     */
    fun createPost(
        title: String,
        content: String
    ): Flow<NetworkResult<Post>>
}
