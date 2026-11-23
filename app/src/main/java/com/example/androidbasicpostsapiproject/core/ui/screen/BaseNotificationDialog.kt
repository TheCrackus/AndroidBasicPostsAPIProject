package com.example.androidbasicpostsapiproject.core.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.androidbasicpostsapiproject.R
import com.example.androidbasicpostsapiproject.core.ui.state.BaseDialog
import com.example.androidbasicpostsapiproject.core.ui.state.BaseDialogType
import com.example.androidbasicpostsapiproject.ui.theme.AndroidBasicPostsAPIProjectTheme

/**
 * Reusable Material 3 notification dialog for displaying informational or success messages on any
 * screen.
 *
 * Resolves localized string resource IDs from [dialog] when provided, falling back to raw text
 * fields or default app string resources. Separates [onDismiss] (dismiss request) from [onConfirm]
 * (confirm button tap, defaulting to [onDismiss]) so callers can optionally attach follow-up
 * actions such as navigating back after acknowledging the dialog.
 *
 * @param dialog Immutable [BaseDialog] payload describing title, message, and confirm label.
 * @param onDismiss Callback invoked when the dialog is dismissed (outside tap or back press).
 * @param onConfirm Callback invoked when the user taps the confirm button.
 */
@Composable
fun BaseNotificationDialog(
    dialog: BaseDialog,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = onDismiss
) {
    val resolvedTitle = dialog.titleRes?.let { stringResource(id = it) }
        ?: dialog.title.ifEmpty { stringResource(id = R.string.default_notification_title) }

    val resolvedMessage = dialog.messageRes?.let { stringResource(id = it) }
        ?: dialog.message

    val resolvedConfirmLabel = dialog.confirmButtonTextRes?.let { stringResource(id = it) }
        ?: dialog.confirmButtonText.ifEmpty { stringResource(id = R.string.btn_accept) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = resolvedTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = resolvedMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(
                    text = resolvedConfirmLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun BaseNotificationDialogPreview() {
    AndroidBasicPostsAPIProjectTheme {
        BaseNotificationDialog(
            dialog = BaseDialog(
                titleRes = R.string.create_post_success_title,
                messageRes = R.string.create_post_success_message,
                confirmButtonTextRes = R.string.btn_accept,
                type = BaseDialogType.SUCCESS
            ),
            onDismiss = {}
        )
    }
}
