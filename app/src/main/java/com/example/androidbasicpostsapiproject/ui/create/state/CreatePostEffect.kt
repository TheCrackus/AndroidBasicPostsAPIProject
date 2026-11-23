package com.example.androidbasicpostsapiproject.ui.create.state

import com.example.androidbasicpostsapiproject.core.ui.state.BaseEffect

/**
 * One-off side effects emitted by [CreatePostViewModel].
 */
sealed interface CreatePostEffect : BaseEffect {
    /** Navigates back after a post has been successfully created on the server. */
    data object NavigateBackWithSuccess : CreatePostEffect

    /** Navigates back when creation was cancelled by the user. */
    data object NavigateBack : CreatePostEffect
}
