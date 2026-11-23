package com.example.androidbasicpostsapiproject.ui.create.state

import com.example.androidbasicpostsapiproject.core.constants.BASE_EMPTY_STRING
import com.example.androidbasicpostsapiproject.core.ui.state.BaseData
import com.example.androidbasicpostsapiproject.core.ui.state.BaseState

/**
 * State representing the Create Post screen following UDF/MVI.
 *
 * @property baseData Core UI flags including loading and error overlays.
 * @property title Text entered in the post title field.
 * @property content Text entered in the post content body field.
 * @property isSubmitEnabled Indicates whether both title and content satisfy validation requirements.
 */
data class CreatePostState(
    override val baseData: BaseData = BaseData(),
    val title: String = BASE_EMPTY_STRING,
    val content: String = BASE_EMPTY_STRING
) : BaseState {
    val isSubmitEnabled: Boolean
        get() = title.isNotBlank() && content.isNotBlank() && !baseData.isLoading
}
