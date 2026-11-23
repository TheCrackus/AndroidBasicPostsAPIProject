package com.example.androidbasicpostsapiproject.domain.usecase

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

/**
 * Domain use case that orchestrates the creation of a new [Post] through [PostRepository].
 *
 * @param repository Source repository providing post creation capabilities.
 */
class CreatePostUseCase(
    private val repository: PostRepository
) {
    /**
     * Executes the post creation and returns a cold [Flow] emitting the progressive [NetworkResult].
     *
     * @param title Title of the post to create.
     * @param content Body content of the post to create.
     */
    operator fun invoke(
        title: String,
        content: String
    ): Flow<NetworkResult<Post>> = repository.createPost(
        title = title,
        content = content
    )
}
