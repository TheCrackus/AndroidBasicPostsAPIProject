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
}
