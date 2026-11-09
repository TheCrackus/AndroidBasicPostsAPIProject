package com.example.androidbasicpostsapiproject.domain.usecase

import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetPostsUseCase(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Post>>> = repository.getPosts()
}
