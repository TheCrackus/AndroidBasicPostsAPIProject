package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.data.remote.datasource.PostRemoteDataSource
import com.example.androidbasicpostsapiproject.data.remote.datasource.PostRemoteDataSourceImpl
import com.example.androidbasicpostsapiproject.data.repository.PostRepositoryImpl
import com.example.androidbasicpostsapiproject.domain.repository.PostRepository
import org.koin.dsl.module

val dataModule = module {
    single<PostRemoteDataSource> {
        PostRemoteDataSourceImpl(apiService = get())
    }

    single<PostRepository> {
        PostRepositoryImpl(remoteDataSource = get())
    }
}
