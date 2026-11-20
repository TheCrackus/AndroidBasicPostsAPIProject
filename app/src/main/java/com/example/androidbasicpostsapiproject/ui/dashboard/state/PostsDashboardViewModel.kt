package com.example.androidbasicpostsapiproject.ui.dashboard.state

import androidx.lifecycle.viewModelScope
import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.core.ui.state.BaseData
import com.example.androidbasicpostsapiproject.core.ui.state.BaseError
import com.example.androidbasicpostsapiproject.core.ui.state.BaseViewModel
import com.example.androidbasicpostsapiproject.domain.usecase.GetPostsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostsDashboardViewModel(
    private val getPostsUseCase: GetPostsUseCase,
) : BaseViewModel<PostsDashboardState, PostsDashboardEvent, PostsDashboardEffect>(
    initialState = PostsDashboardState()
) {
    private var fetchPostsJob: Job? = null

    init {
        onAction(
            event = PostsDashboardEvent.LoadPosts
        )
    }

    override fun onAction(event: PostsDashboardEvent) {
        when (event) {
            PostsDashboardEvent.LoadPosts ->
                fetchPosts()

            PostsDashboardEvent.DismissError ->
                updateState {
                    copy(
                        baseData = baseData.copy(
                            error = null
                        )
                    )
                }

            is PostsDashboardEvent.OnStatusChange ->
                updateState {
                    copy(
                        statusText = event.status
                    )
                }

            PostsDashboardEvent.UpdateStatusClick -> {
                // TODO: Hook for status update API in future phases
            }

            PostsDashboardEvent.NewPostClick ->
                sendEffect(
                    effect = PostsDashboardEffect.NavigateToCreate
                )

            is PostsDashboardEvent.ViewPostClick ->
                sendEffect(
                    effect = PostsDashboardEffect.NavigateToDetail(postId = event.postId)
                )

            is PostsDashboardEvent.EditPostClick ->
                sendEffect(
                    effect = PostsDashboardEffect.NavigateToEdit(postId = event.postId)
                )

            is PostsDashboardEvent.DeletePostClick -> {
                // TODO: Hook for delete post API in future phases
            }
        }
    }

    private fun fetchPosts() {
        fetchPostsJob?.cancel()
        fetchPostsJob = viewModelScope.launch {
            getPostsUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Error ->
                        updateState {
                            copy(
                                baseData = BaseData(
                                    error = BaseError(
                                        errorTitle = result.errorTitle.orEmpty(),
                                        errorBody = result.errorBody.orEmpty(),
                                    )
                                )
                            )
                        }

                    NetworkResult.Loading ->
                        updateState {
                            copy(
                                baseData = BaseData(
                                    isLoading = true
                                )
                            )
                        }

                    is NetworkResult.Success ->
                        updateState {
                            copy(
                                baseData = BaseData(),
                                posts = result.data
                            )
                        }
                }
            }
        }
    }
}
