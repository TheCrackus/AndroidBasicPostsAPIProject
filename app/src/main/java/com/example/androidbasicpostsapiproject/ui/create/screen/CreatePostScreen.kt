package com.example.androidbasicpostsapiproject.ui.create.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.constants.ALPHA_DISABLED_CONTAINER
import com.example.androidbasicpostsapiproject.core.constants.ALPHA_DISABLED_CONTENT
import com.example.androidbasicpostsapiproject.core.constants.ALPHA_PLACEHOLDER
import com.example.androidbasicpostsapiproject.core.constants.BORDER_WIDTH_THIN
import com.example.androidbasicpostsapiproject.core.constants.CORNER_RADIUS_EXTRA_SMALL
import com.example.androidbasicpostsapiproject.core.constants.CORNER_RADIUS_SMALL
import com.example.androidbasicpostsapiproject.core.constants.DIVIDER_THICKNESS_MEDIUM
import com.example.androidbasicpostsapiproject.core.constants.ELEVATION_LARGE
import com.example.androidbasicpostsapiproject.core.constants.POST_CONTENT_INPUT_HEIGHT
import com.example.androidbasicpostsapiproject.core.constants.POST_CONTENT_MAX_LINES
import com.example.androidbasicpostsapiproject.core.constants.POST_CONTENT_MIN_LINES
import com.example.androidbasicpostsapiproject.core.constants.SINGLE_LINE_MAX
import com.example.androidbasicpostsapiproject.core.constants.SPACING_COMPACT
import com.example.androidbasicpostsapiproject.core.constants.SPACING_DEFAULT
import com.example.androidbasicpostsapiproject.core.constants.SPACING_EXTRA_LARGE
import com.example.androidbasicpostsapiproject.core.constants.SPACING_EXTRA_SMALL
import com.example.androidbasicpostsapiproject.core.constants.SPACING_LARGE
import com.example.androidbasicpostsapiproject.core.constants.SPACING_MEDIUM
import com.example.androidbasicpostsapiproject.core.constants.SPACING_SMALL
import com.example.androidbasicpostsapiproject.core.constants.WEIGHT_FILL
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay
import com.example.androidbasicpostsapiproject.core.ui.screen.ObserveAsEvents
import com.example.androidbasicpostsapiproject.ui.create.state.CreatePostEffect
import com.example.androidbasicpostsapiproject.ui.create.state.CreatePostEvent
import com.example.androidbasicpostsapiproject.ui.create.state.CreatePostState
import com.example.androidbasicpostsapiproject.ui.create.state.CreatePostViewModel
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful entry point for the Create Post overlay modal.
 *
 * Obtains [CreatePostViewModel] via Koin, collects its state with lifecycle awareness, observes
 * navigation effects through [ObserveAsEvents], and delegates UI presentation to the stateless
 * [CreatePostScreen].
 *
 * @param viewModel Feature ViewModel handling the creation post state and use case execution.
 * @param onNavigateBack Callback invoked when creation is cancelled.
 * @param onNavigateBackWithSuccess Callback invoked when creation completes successfully.
 */
@Composable
fun CreatePostRoot(
    viewModel: CreatePostViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateBackWithSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(
        flow = viewModel.uiEffects
    ) { effect ->
        when (effect) {
            CreatePostEffect.NavigateBack -> onNavigateBack()
            CreatePostEffect.NavigateBackWithSuccess -> onNavigateBackWithSuccess()
        }
    }

    CreatePostScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

/**
 * Stateless presentation composable for the Create Post modal dialog overlay.
 *
 * Rendered inside a Navigation Compose `dialog` destination so the underlying feed remains
 * visible and darkened.
 *
 * Visual specification aligned with the React MessageNode reference:
 * - A purple bold header "New Post" with a bottom divider line.
 * - Upper-case labeled input fields ("TITLE", "IMAGE", "CONTENT") with crisp borders.
 * - Bottom action buttons: Red text button "CANCEL" and solid purple button "ACCEPT".
 *
 * Enclosed within [BaseScreenOverlay] to provide loading indicators, error dialogs, and generic
 * notification dialogs.
 *
 * @param uiState Current immutable UI state representing form inputs and base status.
 * @param onAction Callback for propagating user events to the ViewModel.
 * @param modifier [Modifier] applied to the card dialog container.
 */
@Composable
fun CreatePostScreen(
    uiState: CreatePostState,
    onAction: (CreatePostEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseScreenOverlay(
        baseData = uiState.baseData,
        onErrorDismiss = {
            onAction(CreatePostEvent.DismissError)
        },
        onDialogDismiss = {
            onAction(CreatePostEvent.DismissDialog)
        },
        onDialogConfirm = {
            onAction(CreatePostEvent.ConfirmSuccessDialog)
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = SPACING_DEFAULT),
            shape = RoundedCornerShape(size = CORNER_RADIUS_SMALL),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = ELEVATION_LARGE
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = SPACING_LARGE,
                            end = SPACING_LARGE,
                            top = SPACING_DEFAULT,
                            bottom = SPACING_MEDIUM
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.create_post_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = DIVIDER_THICKNESS_MEDIUM,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = SPACING_LARGE,
                            vertical = SPACING_DEFAULT
                        )
                ) {
                    Text(
                        text = stringResource(id = R.string.create_post_title_label),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(height = SPACING_SMALL))

                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { newTitle ->
                            onAction(CreatePostEvent.OnTitleChange(title = newTitle))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.create_post_title_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = ALPHA_PLACEHOLDER
                                )
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(height = SPACING_DEFAULT))

                    ImagePickerSection(
                        onSelectImageClick = {
                            onAction(CreatePostEvent.SelectImageClick)
                        }
                    )

                    Spacer(modifier = Modifier.height(height = SPACING_DEFAULT))

                    Text(
                        text = stringResource(id = R.string.create_post_content_label),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(height = SPACING_SMALL))

                    OutlinedTextField(
                        value = uiState.content,
                        onValueChange = { newContent ->
                            onAction(CreatePostEvent.OnContentChange(content = newContent))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = POST_CONTENT_INPUT_HEIGHT),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.create_post_content_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = ALPHA_PLACEHOLDER
                                )
                            )
                        },
                        minLines = POST_CONTENT_MIN_LINES,
                        maxLines = POST_CONTENT_MAX_LINES,
                        shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(height = SPACING_EXTRA_LARGE))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                onAction(CreatePostEvent.CancelClick)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.btn_create_post_cancel),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.width(width = SPACING_MEDIUM))

                        Button(
                            onClick = {
                                onAction(CreatePostEvent.SubmitPost)
                            },
                            enabled = uiState.isSubmitEnabled,
                            shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = ALPHA_DISABLED_CONTAINER
                                ),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = ALPHA_DISABLED_CONTENT
                                )
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.btn_create_post_submit),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePickerSection(
    onSelectImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Connect Photo Picker (PickVisualMedia) and upload image multipart data once the Node.js server supports receiving images
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.create_post_image_label),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(height = SPACING_SMALL))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = BORDER_WIDTH_THIN,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL)
                )
                .padding(
                    horizontal = SPACING_MEDIUM,
                    vertical = SPACING_COMPACT
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onSelectImageClick,
                shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
                border = BorderStroke(
                    width = BORDER_WIDTH_THIN,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.btn_choose_image),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(width = SPACING_MEDIUM))

            Text(
                text = stringResource(id = R.string.create_post_image_placeholder),
                modifier = Modifier.weight(weight = WEIGHT_FILL),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = ALPHA_PLACEHOLDER
                ),
                maxLines = SINGLE_LINE_MAX,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(height = SPACING_EXTRA_SMALL))

        Text(
            text = stringResource(id = R.string.create_post_image_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Create Post Modal - Light", showBackground = true)
@Composable
private fun CreatePostPreview() {
    AndroidBasicPostsAPIProjectTheme(darkTheme = false) {
        CreatePostScreen(
            uiState = CreatePostState(
                title = "Exploring Jetpack Compose",
                content = "Clean Architecture with MVI and Type-Safe navigation in Android."
            ),
            onAction = {}
        )
    }
}

@Preview(name = "Create Post Modal - Dark", showBackground = true)
@Composable
private fun CreatePostDarkPreview() {
    AndroidBasicPostsAPIProjectTheme(darkTheme = true) {
        CreatePostScreen(
            uiState = CreatePostState(
                title = "Exploring Jetpack Compose",
                content = "Clean Architecture with MVI and Type-Safe navigation in Android."
            ),
            onAction = {}
        )
    }
}
