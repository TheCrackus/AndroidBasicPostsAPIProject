package com.example.androidbasicpostsapiproject.core.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats an ISO-8601 post timestamp (e.g. `"2026-11-18T10:00:00.000Z"`) into a compact
 * `"dd/MM/yyyy"` date string for UI display.
 *
 * Returns `"Invalid Date"` when [dateString] is blank, and falls back to the original
 * [dateString] if parsing fails.
 *
 * @param dateString Raw timestamp string received from the backend.
 * @return Formatted `"dd/MM/yyyy"` string, `"Invalid Date"` if blank, or [dateString] on error.
 */
fun formatPostDate(dateString: String): String {
    if (dateString.isBlank()) return "Invalid Date"
    return try {
        val parser = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        )

        val date = parser.parse(dateString)

        if (date != null) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            formatter.format(date)
        } else {
            dateString
        }
    } catch (_: Exception) {
        dateString
    }
}
