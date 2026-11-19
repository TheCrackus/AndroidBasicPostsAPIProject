package com.example.androidbasicpostsapiproject.ui.dashboard.state

import com.example.androidbasicpostsapiproject.core.ui.state.BaseEffect

sealed interface PostsDashboardEffect : BaseEffect {
    data object NavigateToCreate : PostsDashboardEffect
    data class NavigateToDetail(val postId: String) : PostsDashboardEffect
    data class NavigateToEdit(val postId: String) : PostsDashboardEffect
    data class ShowMessage(val message: String) : PostsDashboardEffect
}
