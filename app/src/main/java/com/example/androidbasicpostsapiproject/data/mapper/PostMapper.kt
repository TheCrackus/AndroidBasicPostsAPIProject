package com.example.androidbasicpostsapiproject.data.mapper

import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_CONTENT
import com.example.androidbasicpostsapiproject.core.constants.DEFAULT_POST_TITLE
import com.example.androidbasicpostsapiproject.data.remote.model.CreatorDto
import com.example.androidbasicpostsapiproject.data.remote.model.PostDto
import com.example.androidbasicpostsapiproject.data.remote.model.PostsResponseDto
import com.example.androidbasicpostsapiproject.domain.model.Creator
import com.example.androidbasicpostsapiproject.domain.model.Post

/**
 * Maps a network [CreatorDto] into a pure domain [Creator].
 *
 * Returns `null` when [CreatorDto.name] is missing or blank so the UI layer can provide a
 * localized fallback (e.g. `R.string.default_post_creator`).
 */
fun CreatorDto.toDomain(): Creator? {
    val normalizedName = this.name?.trim()?.takeIf { it.isNotBlank() } ?: return null
    return Creator(name = normalizedName)
}

/**
 * Maps a network [PostDto] into a pure domain [Post].
 *
 * Normalization rules:
 * - Blank or missing `title` / `content` fall back to [DEFAULT_POST_TITLE] / [DEFAULT_POST_CONTENT].
 * - Missing or blank [CreatorDto] and `imageUrl` are normalized to `null` so the UI layer can
 *   provide localized fallbacks (e.g. `R.string.default_post_creator`) or hide optional media
 *   cleanly.
 */
fun PostDto.toDomain(): Post {
    return Post(
        id = this._id.orEmpty(),
        title = this.title?.takeIf { it.isNotBlank() } ?: DEFAULT_POST_TITLE,
        content = this.content?.takeIf { it.isNotBlank() } ?: DEFAULT_POST_CONTENT,
        imageUrl = this.imageUrl?.takeIf { it.isNotBlank() },
        creator = this.creator?.toDomain(),
        createdAt = this.createdAt.orEmpty(),
    )
}

/** Maps a list of [PostDto] items to domain [Post] models. */
fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }

/** Maps a wrapped [PostsResponseDto] envelope to its domain list of [Post] models. */
fun PostsResponseDto.toDomain(): List<Post> = this.posts.orEmpty().toDomain()
