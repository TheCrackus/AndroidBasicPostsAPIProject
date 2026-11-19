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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseScreenOverlay
import com.example.androidbasicpostsapiproject.core.ui.screen.ObserveAsEvents
import com.example.androidbasicpostsapiproject.core.utils.formatPostDate
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostDashboardViewModel
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEffect
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardEvent
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardState
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodeAmber
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodeBorderGray
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodeDeleteRed
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodePurple
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodePurpleDark
import com.example.androidbasicpostsapiproject.ui.theme.MessageNodeTextGray
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostsDashboardRoot(
    viewModel: PostDashboardViewModel = koinViewModel(),
    onNavigateToCreate: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {}
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
                .background(Color.White),
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
                items(
                    items = uiState.posts,
                    key = {
                        it.id
                    }
                ) { post ->
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
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(size = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MessageNodePurple,
                unfocusedBorderColor = MessageNodeBorderGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        TextButton(
            onClick = onUpdateClick,
            shape = RoundedCornerShape(size = 2.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btn_update_status),
                color = MessageNodePurple,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
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
                containerColor = MessageNodeAmber,
                contentColor = Color(0xFF1E1E1E)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.btn_new_post),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E1E1E)
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
    val creatorName = post.creator?.ifBlank {
        stringResource(id = R.string.default_post_creator)
    } ?: stringResource(id = R.string.default_post_creator)

    val formattedDate = formatPostDate(dateString = post.createdAt)

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(size = 4.dp),
        border = BorderStroke(width = 1.dp, color = MessageNodePurple),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White
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
                color = MessageNodeTextGray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.title,
                color = MessageNodePurpleDark,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewClick) {
                    Text(
                        text = stringResource(id = R.string.btn_view_post),
                        color = MessageNodePurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onEditClick) {
                    Text(
                        text = stringResource(id = R.string.btn_edit_post),
                        color = MessageNodePurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(onClick = onDeleteClick) {
                    Text(
                        text = stringResource(id = R.string.btn_delete_post),
                        color = MessageNodeDeleteRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
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
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onReloadClick,
            shape = RoundedCornerShape(size = 2.dp),
            border = BorderStroke(width = 1.dp, color = MessageNodePurple)
        ) {
            Text(
                text = stringResource(id = R.string.btn_reload_posts),
                color = MessageNodePurple,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostsDashboardPreview() {
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
                        creator = "Edgar",
                        createdAt = "Invalid Date"
                    ),
                    Post(
                        id = "2",
                        title = "Second Awesome Post",
                        content = "Exploring the full potential of Android Jetpack Compose.",
                        imageUrl = null,
                        creator = "Admin",
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
fun PostsDashboardEmptyPreview() {
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
