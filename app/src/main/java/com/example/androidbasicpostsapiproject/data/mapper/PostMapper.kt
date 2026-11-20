package com.example.androidbasicpostsapiproject.data.mapper

import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_CONTENT
import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_TITLE
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto
import com.example.androidbasicpostsapiproject.data.remote.model.PostsResponseDto
import com.example.androidbasicpostsapiproject.domain.model.Post

fun PostDto.toDomain(): Post {
    return Post(
        id = this._id.orEmpty(),
        title = this.title?.takeIf { it.isNotBlank() } ?: DEFAULT_POST_TITLE,
        content = this.content?.takeIf { it.isNotBlank() } ?: DEFAULT_POST_CONTENT,
        imageUrl = this.imageUrl?.takeIf { it.isNotBlank() },
        creator = this.creator?.takeIf { it.isNotBlank() },
        createdAt = this.createdAt.orEmpty(),
    )
}

fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }

fun PostsResponseDto.toDomain(): List<Post> = this.posts.orEmpty().toDomain()
