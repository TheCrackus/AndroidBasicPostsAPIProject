package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.BuildConfig
import com.example.androidbasicpostsapiproject.core.constants.BASE_URL
import com.example.androidbasicpostsapiproject.core.constants.JSON_MEDIA_TYPE
import com.example.androidbasicpostsapiproject.core.constants.POST_TIMEOUT_SECONDS
import com.example.androidbasicpostsapiproject.core.coroutines.DefaultDispatcherProvider
import com.example.androidbasicpostsapiproject.core.coroutines.DispatcherProvider
import com.example.androidbasicpostsapiproject.data.remote.api.PostsApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single<DispatcherProvider> {
        DefaultDispatcherProvider()
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(POST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(POST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
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
            .addConverterFactory(
                get<Json>().asConverterFactory(JSON_MEDIA_TYPE.toMediaType())
            )
            .build()
    }

    single<PostsApiService> {
        get<Retrofit>().create(PostsApiService::class.java)
    }
}
