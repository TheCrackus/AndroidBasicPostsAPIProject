package com.example.androidbasicpostsapiproject.core.utils

import java.text.SimpleDateFormat
import java.util.Locale

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
