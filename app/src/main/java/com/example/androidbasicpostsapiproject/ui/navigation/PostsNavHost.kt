package com.example.androidbasicpostsapiproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.example.androidbasicpostsapiproject.core.constants.NAV_RESULT_POST_CREATED
import com.example.androidbasicpostsapiproject.ui.create.screen.CreatePostRoot
import com.example.androidbasicpostsapiproject.ui.dashboard.screen.PostsDashboardRoot
import kotlinx.serialization.Serializable

/** Type-safe navigation route for the main posts feed dashboard. */
@Serializable
object PostsDashboard

/** Type-safe navigation route for creating a new post. */
@Serializable
object CreatePost

/**
 * Root Navigation Compose graph defining the app's type-safe destinations.
 *
 * Owns all [NavHostController] interactions while hoisting navigation callbacks out of individual
 * `*Root` screens to keep feature screens decoupled from the navigation controller and keeping
 * this graph free of `ViewModel` instances. Cross-destination results (such as refreshing the feed
 * after creating a post) are passed through `SavedStateHandle` on the back stack entry, observing
 * results at [Lifecycle.State.RESUMED] per Navigation Compose guidance for dialog destinations.
 *
 * @param navHostController Controller managing the back stack and navigation transitions.
 * @param modifier [Modifier] applied to the underlying [NavHost].
 */
@Composable
fun PostsNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = PostsDashboard,
        modifier = modifier
    ) {
        composable<PostsDashboard> { backStackEntry ->
            val shouldRefreshPosts by backStackEntry.savedStateHandle
                .getStateFlow(
                    key = NAV_RESULT_POST_CREATED,
                    initialValue = false
                )
                .collectAsStateWithLifecycle(
                    lifecycle = backStackEntry.lifecycle,
                    minActiveState = Lifecycle.State.RESUMED
                )

            PostsDashboardRoot(
                shouldRefreshPosts = shouldRefreshPosts,
                onRefreshConsumed = {
                    backStackEntry.savedStateHandle[NAV_RESULT_POST_CREATED] = false
                },
                onNavigateToCreate = {
                    navHostController.navigate(route = CreatePost)
                }
            )
        }

        dialog<CreatePost> {
            CreatePostRoot(
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onNavigateBackWithSuccess = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            key = NAV_RESULT_POST_CREATED,
                            value = true
                        )
                    navHostController.popBackStack()
                }
            )
        }
    }
}
