package com.example.androidbasicpostsapiproject.di

import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(
        constructor = ::PostDashboardViewModel
    )
}
