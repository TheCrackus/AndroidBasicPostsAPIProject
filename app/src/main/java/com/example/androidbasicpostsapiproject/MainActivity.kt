package com.example.androidbasicpostsapiproject

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseContainer
import com.example.androidbasicpostsapiproject.domain.model.Creator
import com.example.androidbasicpostsapiproject.domain.model.Post
import com.example.androidbasicpostsapiproject.ui.dashboard.screen.PostsDashboardScreen
import com.example.androidbasicpostsapiproject.ui.dashboard.state.PostsDashboardState
import com.example.androidbasicpostsapiproject.ui.navigation.PostsNavHost
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme

/**
 * Single-activity entry point of the application.
 *
 * Enables edge-to-edge rendering and hosts the root [PostsApp] shell backed by [PostsNavHost].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            PostsApp {
                PostsNavHost(
                    navHostController = navController
                )
            }
        }
    }
}

/**
 * Root Compose shell that wraps screen content with [AndroidBasicPostsAPIProjectTheme] and
 * [BaseContainer].
 *
 * Uses the Compose Slot API pattern (`content`) so the complete application chrome (theme,
 * MessageNode `TopAppBar`, and scaffold insets) can be previewed and tested independently of
 * navigation destinations or DI-backed ViewModels.
 *
 * @param modifier [Modifier] applied to the underlying [BaseContainer].
 * @param content Navigation graph or stateless screen rendered inside the app shell.
 */
@Composable
fun PostsApp(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AndroidBasicPostsAPIProjectTheme {
        BaseContainer(
            modifier = modifier.fillMaxSize(),
            content = content
        )
    }
}

@Preview(
    name = "Light",
    showSystemUi = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true
)
@Composable
private fun PostsAppPreview() {
    PostsApp {
        PostsDashboardScreen(
            uiState = PostsDashboardState(
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
                    ),
                    Post(
                        id = "2",
                        title = "Second Awesome Post",
                        content = "Exploring the full potential of Android Jetpack Compose.",
                        imageUrl = null,
                        creator = Creator(
                            name = "Admin"
                        ),
                        createdAt = "2026-11-19T15:30:00.000Z"
                    )
                )
            ),
            onAction = {}
        )
    }
}
