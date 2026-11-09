package com.example.androidbasicpostsapiproject.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val creator: String?,
    val createdAt: String
)
