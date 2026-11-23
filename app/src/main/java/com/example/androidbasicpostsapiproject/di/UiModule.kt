package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.ui.create.state.CreatePostViewModel
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(
        constructor = ::PostsDashboardViewModel
    )
    viewModelOf(
        constructor = ::CreatePostViewModel
    )
}
