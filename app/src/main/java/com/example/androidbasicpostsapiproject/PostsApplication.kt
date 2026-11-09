package com.example.androidbasicpostsapiproject

import android.app.Application
import com.example.androidbasicpostsapiproject.di.dataModule
import com.example.androidbasicpostsapiproject.di.domainModule
import com.example.androidbasicpostsapiproject.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PostsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PostsApplication)
            modules(
                listOf(
                    networkModule,
                    dataModule,
                    domainModule
                )
            )
        }
    }
}
