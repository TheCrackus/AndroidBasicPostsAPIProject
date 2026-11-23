package com.example.androidbasicpostsapiproject.ui.dashboard.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.constants.BASE_EMPTY_STRING
import com.example.androidbasicpostsapiproject.core.constants.BORDER_WIDTH_THIN
import com.example.androidbasicpostsapiproject.core.constants.CORNER_RADIUS_EXTRA_SMALL
import com.example.androidbasicpostsapiproject.core.constants.CORNER_RADIUS_SMALL
import com.example.androidbasicpostsapiproject.core.constants.DASHBOARD_KEY_EMPTY_POSTS
import com.example.androidbasicpostsapiproject.core.constants.DASHBOARD_KEY_NEW_POST_BUTTON
import com.example.androidbasicpostsapiproject.core.constants.DASHBOARD_KEY_POST_FALLBACK_PREFIX
import com.example.androidbasicpostsapiproject.core.constants.DASHBOARD_KEY_STATUS_SECTION
import com.example.androidbasicpostsapiproject.core.constants.ELEVATION_MEDIUM
import com.example.androidbasicpostsapiproject.core.constants.ELEVATION_SMALL
import com.example.androidbasicpostsapiproject.core.constants.SPACING_COMPACT
import com.example.androidbasicpostsapiproject.core.constants.SPACING_DEFAULT
import com.example.androidbasicpostsapiproject.core.constants.SPACING_EXTRA_LARGE
import com.example.androidbasicpostsapiproject.core.constants.SPACING_LARGE
import com.example.androidbasicpostsapiproject.core.constants.SPACING_MEDIUM
import com.example.androidbasicpostsapiproject.core.constants.SPACING_MEDIUM_SMALL
import com.example.androidbasicpostsapiproject.core.constants.SPACING_SECTION
import com.example.androidbasicpostsapiproject.core.constants.SPACING_SMALL
import com.example.androidbasicpostsapiproject.core.constants.WEIGHT_FILL
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay
import com.example.androidbasicpostsapiproject.core.ui.screen.ObserveAsEvents
import com.example.androidbasicpostsapiproject.core.utils.formatPostDate
import com.example.androidbasicpostsapiproject.domain.model.Creator
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEffect
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEvent
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardState
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardViewModel
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful entry point for the posts dashboard destination.
 *
 * Obtains [PostsDashboardViewModel] via Koin, collects [PostsDashboardViewModel.uiState] with
 * lifecycle awareness, observes one-off navigation effects through [ObserveAsEvents], reacts to
 * navigation refresh signals via [shouldRefreshPosts], and delegates rendering to the stateless
 * [PostsDashboardScreen].
 *
 * @param viewModel Feature ViewModel scoped to this navigation destination.
 * @param shouldRefreshPosts `true` when a child destination (e.g. Create Post) returned a result
 * requesting a feed reload.
 * @param onRefreshConsumed Callback invoked right after dispatching [PostsDashboardEvent.LoadPosts]
 * so the navigation result flag is reset and will not re-trigger on configuration changes.
 * @param onNavigateToCreate Callback triggered when the user requests creating a new post.
 * @param onNavigateToDetail Callback triggered with the selected post ID to view its details.
 * @param onNavigateToEdit Callback triggered with the selected post ID to edit it.
 */
@Composable
fun PostsDashboardRoot(
    viewModel: PostsDashboardViewModel = koinViewModel(),
    shouldRefreshPosts: Boolean = false,
    onRefreshConsumed: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = shouldRefreshPosts) {
        if (shouldRefreshPosts) {
            viewModel.onAction(event = PostsDashboardEvent.LoadPosts)
            onRefreshConsumed()
        }
    }

    ObserveAsEvents(
        flow = viewModel.uiEffects
    ) { effect ->
        when (effect) {
            is PostsDashboardEffect.NavigateToCreate -> onNavigateToCreate()
            is PostsDashboardEffect.NavigateToDetail -> onNavigateToDetail(effect.postId)
            is PostsDashboardEffect.NavigateToEdit -> onNavigateToEdit(effect.postId)
            is PostsDashboardEffect.ShowMessage -> Unit
        }
    }

    PostsDashboardScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

/**
 * Stateless presentation composable for the MessageNode posts dashboard.
 *
 * Renders the status input bar, new-post CTA, and scrollable feed (or empty state) wrapped in
 * [BaseScreenOverlay] for loading and error handling. Designed to be independently previewable and
 * UI-testable without a ViewModel or DI graph.
 *
 * @param uiState Current immutable snapshot of the dashboard UI state.
 * @param onAction Dispatcher callback invoked for every user [PostsDashboardEvent].
 * @param modifier [Modifier] applied to the feed [LazyColumn].
 */
@Composable
fun PostsDashboardScreen(
    uiState: PostsDashboardState,
    onAction: (PostsDashboardEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseScreenOverlay(
        baseData = uiState.baseData,
        onErrorDismiss = {
            onAction(
                PostsDashboardEvent.DismissError
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                horizontal = SPACING_LARGE,
                vertical = SPACING_EXTRA_LARGE
            ),
            verticalArrangement = Arrangement.spacedBy(space = SPACING_LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = DASHBOARD_KEY_STATUS_SECTION) {
                StatusUpdateSection(
                    statusText = uiState.statusText,
                    onStatusChange = {
                        onAction(
                            PostsDashboardEvent.OnStatusChange(
                                status = it
                            )
                        )
                    },
                    onUpdateClick = {
                        onAction(
                            PostsDashboardEvent.UpdateStatusClick
                        )
                    }
                )
            }

            item(key = DASHBOARD_KEY_NEW_POST_BUTTON) {
                NewPostActionButton(
                    onClick = {
                        onAction(
                            PostsDashboardEvent.NewPostClick
                        )
                    }
                )
            }

            if (uiState.posts.isEmpty() && !uiState.baseData.isLoading) {
                item(key = DASHBOARD_KEY_EMPTY_POSTS) {
                    EmptyPostsSection(
                        onReloadClick = {
                            onAction(
                                PostsDashboardEvent.LoadPosts
                            )
                        }
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.posts,
                    key = { index, post ->
                        post.id.ifBlank { "$DASHBOARD_KEY_POST_FALLBACK_PREFIX$index" }
                    }
                ) { _, post ->
                    PostItemCard(
                        post = post,
                        onViewClick = {
                            onAction(
                                PostsDashboardEvent.ViewPostClick(
                                    postId = post.id
                                )
                            )
                        },
                        onEditClick = {
                            onAction(
                                PostsDashboardEvent.EditPostClick(
                                    postId = post.id
                                )
                            )
                        },
                        onDeleteClick = {
                            onAction(
                                PostsDashboardEvent.DeletePostClick(
                                    postId = post.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusUpdateSection(
    statusText: String,
    onStatusChange: (String) -> Unit,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SPACING_COMPACT),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = statusText,
            onValueChange = onStatusChange,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.status_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.weight(weight = WEIGHT_FILL)
        )

        Spacer(modifier = Modifier.width(width = SPACING_MEDIUM))

        TextButton(
            onClick = onUpdateClick,
            shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL)
        ) {
            Text(
                text = stringResource(id = R.string.btn_update_status),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun NewPostActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = ELEVATION_SMALL,
                pressedElevation = ELEVATION_MEDIUM
            ),
            contentPadding = PaddingValues(
                horizontal = SPACING_EXTRA_LARGE,
                vertical = SPACING_MEDIUM_SMALL
            )
        ) {
            Text(
                text = stringResource(id = R.string.btn_new_post),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
private fun PostItemCard(
    post: Post,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val creatorName = post.creator?.name ?: stringResource(id = R.string.default_post_creator)

    val formattedDate = formatPostDate(dateString = post.createdAt)
        ?: stringResource(id = R.string.invalid_post_date)

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = CORNER_RADIUS_SMALL),
        border = BorderStroke(
            width = BORDER_WIDTH_THIN,
            color = MaterialTheme.colorScheme.primary
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = SPACING_DEFAULT)
        ) {
            Text(
                text = stringResource(
                    id = R.string.post_header_format,
                    creatorName,
                    formattedDate
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(height = SPACING_MEDIUM_SMALL))

            Text(
                text = post.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(height = SPACING_DEFAULT))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewClick) {
                    Text(
                        text = stringResource(id = R.string.btn_view_post),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.width(width = SPACING_SMALL))

                TextButton(onClick = onEditClick) {
                    Text(
                        text = stringResource(id = R.string.btn_edit_post),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.width(width = SPACING_SMALL))

                TextButton(onClick = onDeleteClick) {
                    Text(
                        text = stringResource(id = R.string.btn_delete_post),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPostsSection(
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SPACING_SECTION),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.empty_posts_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(height = SPACING_MEDIUM))

        OutlinedButton(
            onClick = onReloadClick,
            shape = RoundedCornerShape(size = CORNER_RADIUS_EXTRA_SMALL),
            border = BorderStroke(
                width = BORDER_WIDTH_THIN,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(id = R.string.btn_reload_posts),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostsDashboardPreview() {
    AndroidBasicPostsAPIProjectTheme {
        PostsDashboardScreen(
            uiState = PostsDashboardState(
                statusText = BASE_EMPTY_STRING,
                posts = listOf(
                    Post(
                        id = "1",
                        title = "First Post",
                        content = "This is the first post created in the system!",
                        imageUrl = null,
                        creator = Creator(
                            name = "Edgar"
                        ),
                        createdAt = BASE_EMPTY_STRING
                    ),
                    Post(
                        id = "2",
                        title = "Second Awesome Post",
                        content = "Exploring the full potential of Android Jetpack Compose.",
                        imageUrl = null,
                        creator = Creator(
                            name = "Admin"
                        ),
                        createdAt = "2026-11-18T10:00:00.000Z"
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostsDashboardDarkPreview() {
    AndroidBasicPostsAPIProjectTheme(darkTheme = true) {
        PostsDashboardScreen(
            uiState = PostsDashboardState(
                statusText = BASE_EMPTY_STRING,
                posts = listOf(
                    Post(
                        id = "1",
                        title = "First Post",
                        content = "This is the first post created in the system!",
                        imageUrl = null,
                        creator = Creator(
                            name = "Edgar"
                        ),
                        createdAt = "2026-11-18T10:00:00.000Z"
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostsDashboardEmptyPreview() {
    AndroidBasicPostsAPIProjectTheme {
        PostsDashboardScreen(
            uiState = PostsDashboardState(
                statusText = BASE_EMPTY_STRING,
                posts = emptyList()
            ),
            onAction = {}
        )
    }
}
