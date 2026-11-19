package com.example.androidbasicpostsapiproject.ui.dashboard.state

import com.example.androidbasicpostsapiproject.core.ui.state.BaseEvent

sealed interface PostsDashboardEvent : BaseEvent {
    data object LoadPosts : PostsDashboardEvent
    data object DismissError : PostsDashboardEvent
    data class OnStatusChange(val status: String) : PostsDashboardEvent
    data object UpdateStatusClick : PostsDashboardEvent
    data object NewPostClick : PostsDashboardEvent
    data class ViewPostClick(val postId: String) : PostsDashboardEvent
    data class EditPostClick(val postId: String) : PostsDashboardEvent
    data class DeletePostClick(val postId: String) : PostsDashboardEvent
}
