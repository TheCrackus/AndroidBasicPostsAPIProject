package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.domain.usecase.CreatePostUseCase
import com.example.androidbasicpostsapiproject.domain.usecase.GetPostsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetPostsUseCase(repository = get()) }
    factory { CreatePostUseCase(repository = get()) }
}
