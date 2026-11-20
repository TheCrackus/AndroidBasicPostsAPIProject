package com.example.androidbasicpostsapiproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidbasicpostsapiproject.ui.dashboard.screen.PostsDashboardRoot
import kotlinx.serialization.Serializable

@Serializable
object PostsDashboard

@Composable
fun PostsNavHost(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = PostsDashboard
    ) {
        composable<PostsDashboard> {
            /**
             * TODO: Implement nav lambdas here
             */
            PostsDashboardRoot()
        }
    }
}
