package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.core.constants.BASE_URL
import com.example.androidbasicpostsapiproject.core.constants.POST_TIME_OUT_MILLIS
import com.example.androidbasicpostsapiproject.data.remote.api.PostsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(POST_TIME_OUT_MILLIS, TimeUnit.SECONDS)
            .readTimeout(POST_TIME_OUT_MILLIS, TimeUnit.SECONDS)
            .build()
    }

    single {
        /**
         * Emulator: 10.0.2.2 - PC Local host
         * Change to real URL when server is deployed
         */
        val baseUrl = BASE_URL

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<PostsApiService> {
        get<Retrofit>().create(PostsApiService::class.java)
    }
}
