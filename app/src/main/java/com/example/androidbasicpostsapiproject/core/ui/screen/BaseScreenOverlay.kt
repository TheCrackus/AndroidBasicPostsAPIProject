package com.example.androidbasicpostsapiproject.core.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.ui.state.BaseData

@Composable
fun BaseScreenOverlay(
    baseData: BaseData,
    onErrorDismiss: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content()

        if (baseData.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }

        baseData.error?.let { error ->
            AlertDialog(
                onDismissRequest = onErrorDismiss,
                title = {
                    Text(
                        text = error.errorTitle.ifEmpty {
                            stringResource(id = R.string.default_error_title)
                        }
                    )
                },
                text = {
                    Text(
                        text = error.errorBody
                    )
                },
                confirmButton = {
                    Button(onClick = onErrorDismiss) {
                        Text(
                            text = stringResource(id = R.string.ok_text_button)
                        )
                    }
                }
            )
        }
    }
}
