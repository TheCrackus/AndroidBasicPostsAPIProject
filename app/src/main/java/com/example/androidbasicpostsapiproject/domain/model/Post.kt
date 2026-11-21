package com.example.androidbasicpostsapiproject.domain.model

data class Creator(
    val name: String,
)

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val creator: Creator?,
    val createdAt: String,
)
