package com.example.androidbasicpostsapiproject.ui.create.state

import androidx.lifecycle.viewModelScope
import com.example.androidbasicpostsapiproject.core.network.NetworkResult
import com.example.androidbasicpostsapiproject.core.ui.state.BaseData
import com.example.androidbasicpostsapiproject.core.ui.state.BaseError
import com.example.androidbasicpostsapiproject.core.ui.state.BaseViewModel
import com.example.androidbasicpostsapiproject.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel managing the Create Post flow following the [BaseViewModel] contract.
 *
 * Enforces in-flight job cancellation before initiating creation to prevent duplicate submissions
 * caused by rapid button presses.
 *
 * @param createPostUseCase Domain use case used to submit post creation to the repository.
 */
class CreatePostViewModel(
    private val createPostUseCase: CreatePostUseCase
) : BaseViewModel<CreatePostState, CreatePostEvent, CreatePostEffect>(
    initialState = CreatePostState()
) {
    private var submitJob: Job? = null

    override fun onAction(event: CreatePostEvent) {
        when (event) {
            is CreatePostEvent.OnTitleChange ->
                updateState {
                    copy(title = event.title)
                }

            is CreatePostEvent.OnContentChange ->
                updateState {
                    copy(content = event.content)
                }

            CreatePostEvent.SubmitPost ->
                submitPost()

            CreatePostEvent.DismissError ->
                updateState {
                    copy(
                        baseData = baseData.copy(error = null)
                    )
                }

            CreatePostEvent.CancelClick ->
                sendEffect(effect = CreatePostEffect.NavigateBack)
        }
    }

    private fun submitPost() {
        val currentState = uiState.value
        if (!currentState.isSubmitEnabled) return

        submitJob?.cancel()
        submitJob = viewModelScope.launch {
            createPostUseCase(
                title = currentState.title.trim(),
                content = currentState.content.trim()
            ).collect { result ->
                when (result) {
                    NetworkResult.Loading ->
                        updateState {
                            copy(
                                baseData = BaseData(isLoading = true)
                            )
                        }

                    is NetworkResult.Success -> {
                        updateState {
                            copy(baseData = BaseData())
                        }
                        sendEffect(effect = CreatePostEffect.NavigateBackWithSuccess)
                    }

                    is NetworkResult.Error ->
                        updateState {
                            copy(
                                baseData = BaseData(
                                    error = BaseError(
                                        errorTitle = result.errorTitle.orEmpty(),
                                        errorBody = result.errorBody.orEmpty()
                                    )
                                )
                            )
                        }
                }
            }
        }
    }
}
