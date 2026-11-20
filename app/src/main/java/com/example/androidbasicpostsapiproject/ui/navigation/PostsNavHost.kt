package com.example.androidbasicpostsapiproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidbasicpostsapiproject.ui.dashboard.screen.PostsDashboardRoot
import kotlinx.serialization.Serializable

/** Type-safe navigation route for the main posts feed dashboard. */
@Serializable
object PostsDashboard

/**
 * Root Navigation Compose graph defining the app's type-safe destinations.
 *
 * Owns all [NavHostController] interactions while hoisting navigation callbacks out of individual
 * `*Root` screens to keep feature screens decoupled from the navigation controller.
 *
 * @param navHostController Controller managing the back stack and navigation transitions.
 * @param modifier [Modifier] applied to the underlying [NavHost].
 */
@Composable
fun PostsNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = PostsDashboard,
        modifier = modifier,
    ) {
        composable<PostsDashboard> {
            // TODO: Implement navigation lambdas when detail/create/edit routes are added
            PostsDashboardRoot()
        }
    }
}
