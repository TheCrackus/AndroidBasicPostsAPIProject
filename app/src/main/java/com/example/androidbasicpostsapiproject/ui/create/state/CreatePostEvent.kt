package com.example.androidbasicpostsapiproject.ui.create.state

import com.example.androidbasicpostsapiproject.core.ui.state.BaseEvent

/**
 * User actions and events for the Create Post screen.
 */
sealed interface CreatePostEvent : BaseEvent {
    /** Triggered when the user modifies the post title input. */
    data class OnTitleChange(val title: String) : CreatePostEvent

    /** Triggered when the user modifies the post content body input. */
    data class OnContentChange(val content: String) : CreatePostEvent

    /** Triggered when the user taps the submit / accept button to publish the post. */
    data object SubmitPost : CreatePostEvent

    /** Triggered when the user dismisses the error dialog overlay. */
    data object DismissError : CreatePostEvent

    /** Triggered when the user taps the cancel button to abandon creation. */
    data object CancelClick : CreatePostEvent
}
