package com.example.androidbasicpostsapiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.androidbasicpostsapiproject.core.ui.screen.BaseContainer
import com.example.androidbasicpostsapiproject.ui.navigation.BaseNavController
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            AndroidBasicPostsAPIProjectTheme {
                BaseContainer(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    BaseNavController(
                        navHostController = navController
                    )
                }
            }
        }
    }
}
