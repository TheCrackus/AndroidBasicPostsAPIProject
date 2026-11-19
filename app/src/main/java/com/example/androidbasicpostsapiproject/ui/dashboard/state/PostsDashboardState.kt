package com.example.androidbasicpostsapiproject.ui.dashboard.state

import com.example.androidbasicpostsapiproject.core.ui.state.BaseData
import com.example.androidbasicpostsapiproject.core.ui.state.BaseState
import com.example.androidbasicpostsapiproject.domain.model.Post

data class PostsDashboardState(
    override val baseData: BaseData = BaseData(),
    val posts: List<Post> = emptyList(),
    val statusText: String = ""
) : BaseState
