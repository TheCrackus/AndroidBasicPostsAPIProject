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

/**
 * Shared screen wrapper that layers common loading, error, and notification dialog UI over
 * [content] based on [baseData].
 *
 * Displays a centered [CircularProgressIndicator] when [BaseData.isLoading] is `true`, an error
 * [AlertDialog] when [BaseData.error] is non-null, and a [BaseNotificationDialog] when
 * [BaseData.dialog] is non-null.
 *
 * @param baseData Common loading, error, and dialog state for the wrapped screen.
 * @param onErrorDismiss Callback invoked when the user dismisses or confirms the error dialog.
 * @param onDialogDismiss Callback invoked when the user dismisses a notification dialog.
 * @param onDialogConfirm Callback invoked when the user taps the confirm button on a notification
 * dialog (defaults to [onDialogDismiss]).
 * @param content Underlying stateless screen content.
 */
@Composable
fun BaseScreenOverlay(
    baseData: BaseData,
    onErrorDismiss: () -> Unit = {},
    onDialogDismiss: () -> Unit = {},
    onDialogConfirm: () -> Unit = onDialogDismiss,
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

        baseData.dialog?.let { dialog ->
            BaseNotificationDialog(
                dialog = dialog,
                onDismiss = onDialogDismiss,
                onConfirm = onDialogConfirm
            )
        }
    }
}
