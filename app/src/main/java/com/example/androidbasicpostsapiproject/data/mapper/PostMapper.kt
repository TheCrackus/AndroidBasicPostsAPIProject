package com.example.androidbasicpostsapiproject.data.mapper

import com.example.androidbasicpostsapiproject.data.remote.model.PostDto
import com.example.androidbasicpostsapiproject.domain.model.Post

fun PostDto.toDomain(): Post {
    return Post(
        id = this._id.orEmpty(),
        title = this.title ?: "Sin título",
        content = this.content ?: "Sin contenido",
        imageUrl = this.imageUrl.orEmpty(),
        creator = this.creator ?: "Anónimo",
        createdAt = this.createdAt.orEmpty()
    )
}

fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }
