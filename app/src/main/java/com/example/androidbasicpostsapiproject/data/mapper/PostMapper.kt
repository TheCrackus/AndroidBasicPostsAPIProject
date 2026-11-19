package com.example.androidbasicpostsapiproject.data.mapper

import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_CONTENT
import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_CREATOR
import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_TITLE
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto
import com.example.androidbasicpostsapiproject.domain.model.Post

fun PostDto.toDomain(): Post {
    return Post(
        id = this._id.orEmpty(),
        title = this.title ?: DEFAULT_POST_TITLE,
        content = this.content ?: DEFAULT_POST_CONTENT,
        imageUrl = this.imageUrl.orEmpty(),
        creator = this.creator ?: DEFAULT_POST_CREATOR,
        createdAt = this.createdAt.orEmpty()
    )
}

fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }
