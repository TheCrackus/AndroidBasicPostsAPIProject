package com.example.androidbasicpostsapiproject.core.utils

import com.example.androidbasicpostsapiproject.core.constants.DISPLAY_DATE_PATTERN
import com.example.androidbasicpostsapiproject.core.constants.ISO_DATE_TIME_PATTERN
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats an ISO-8601 post timestamp (e.g. `"2026-11-18T10:00:00.000Z"`) into a compact
 * `"dd/MM/yyyy"` date string for UI display.
 *
 * Returns `null` when [dateString] is blank so the UI layer can supply a localized fallback
 * resource, and falls back to the original [dateString] if parsing fails.
 *
 * @param dateString Raw timestamp string received from the backend.
 * @return Formatted `"dd/MM/yyyy"` string, `null` if blank, or [dateString] on parse error.
 */
fun formatPostDate(dateString: String): String? {
    if (dateString.isBlank()) return null
    return try {
        val parser = SimpleDateFormat(
            ISO_DATE_TIME_PATTERN,
            Locale.getDefault()
        )

        val date = parser.parse(dateString)

        if (date != null) {
            val formatter = SimpleDateFormat(
                DISPLAY_DATE_PATTERN,
                Locale.getDefault()
            )

            formatter.format(date)
        } else {
            dateString
        }
    } catch (_: Exception) {
        dateString
    }
}
