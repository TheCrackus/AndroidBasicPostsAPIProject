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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay
import com.example.androidbasicpostsapiproject.core.ui.screen.ObserveAsEvents
import com.example.androidbasicpostsapiproject.core.utils.formatPostDate
import com.example.androidbasicpostsapiproject.domain.model.Creator
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardViewModel
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEffect
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEvent
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardState
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful entry point for the posts dashboard destination.
 *
 * Obtains [PostsDashboardViewModel] via Koin, collects [PostsDashboardViewModel.uiState] with
 * lifecycle awareness, observes one-off navigation effects through [ObserveAsEvents], and
 * delegates rendering to the stateless [PostsDashboardScreen].
 *
 * @param viewModel Feature ViewModel scoped to this navigation destination.
 * @param onNavigateToCreate Callback triggered when the user requests creating a new post.
 * @param onNavigateToDetail Callback triggered with the selected post ID to view its details.
 * @param onNavigateToEdit Callback triggered with the selected post ID to edit it.
 */
@Composable
fun PostsDashboardRoot(
    viewModel: PostsDashboardViewModel = koinViewModel(),
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "status_section") {
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

            item(key = "new_post_button") {
                NewPostActionButton(
                    onClick = {
                        onAction(
                            PostsDashboardEvent.NewPostClick
                        )
                    }
                )
            }

            if (uiState.posts.isEmpty() && !uiState.baseData.isLoading) {
                item(key = "empty_posts_state") {
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
                        post.id.ifBlank { "post_fallback_$index" }
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
            .padding(horizontal = 8.dp),
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
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(size = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        TextButton(
            onClick = onUpdateClick,
            shape = RoundedCornerShape(size = 2.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btn_update_status),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
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
            shape = RoundedCornerShape(size = 2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btn_new_post),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondary,
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

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 4.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.post_header_format,
                    creatorName,
                    formattedDate
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(height = 10.dp))

            Text(
                text = post.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewClick) {
                    Text(
                        text = stringResource(id = R.string.btn_view_post),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onEditClick) {
                    Text(
                        text = stringResource(id = R.string.btn_edit_post),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onDeleteClick) {
                    Text(
                        text = stringResource(id = R.string.btn_delete_post),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
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
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.empty_posts_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onReloadClick,
            shape = RoundedCornerShape(size = 2.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.btn_reload_posts),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
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
                statusText = "",
                posts = listOf(
                    Post(
                        id = "1",
                        title = "First Post",
                        content = "This is the first post created in the system!",
                        imageUrl = null,
                        creator = Creator(
                            name = "Edgar"
                        ),
                        createdAt = "Invalid Date"
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
                statusText = "",
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
                statusText = "",
                posts = emptyList()
            ),
            onAction = {}
        )
    }
}
