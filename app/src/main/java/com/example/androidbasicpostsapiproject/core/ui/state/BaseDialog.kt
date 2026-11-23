package com.example.androidbasicpostsapiproject.core.ui.state

import androidx.annotation.StringRes
import com.example.androidbasicpostsapiproject.core.constants.BASE_EMPTY_STRING

/**
 * Categorizes presentation-level notification dialogs rendered by
 * [com.example.androidbasicpostsapiproject.core.ui.screen.BaseNotificationDialog].
 */
enum class BaseDialogType {
    INFO,
    SUCCESS
}

/**
 * UI-ready representation of a generic notification dialog that can be displayed on any screen.
 *
 * Supports both localized string resource IDs (`*Res`) and runtime text values. When a resource ID
 * is provided, it takes precedence over the corresponding raw string; if neither is provided, the
 * dialog composable falls back to default localized strings.
 *
 * @property title Raw dialog title used when [titleRes] is `null`.
 * @property message Raw dialog body used when [messageRes] is `null`.
 * @property confirmButtonText Raw confirm button label used when [confirmButtonTextRes] is `null`.
 * @property titleRes Optional string resource ID for the dialog title.
 * @property messageRes Optional string resource ID for the dialog message body.
 * @property confirmButtonTextRes Optional string resource ID for the confirm button label.
 * @property type Semantic category of the notification dialog.
 */
data class BaseDialog(
    val title: String = BASE_EMPTY_STRING,
    val message: String = BASE_EMPTY_STRING,
    val confirmButtonText: String = BASE_EMPTY_STRING,
    @param:StringRes val titleRes: Int? = null,
    @param:StringRes val messageRes: Int? = null,
    @param:StringRes val confirmButtonTextRes: Int? = null,
    val type: BaseDialogType = BaseDialogType.INFO
)
