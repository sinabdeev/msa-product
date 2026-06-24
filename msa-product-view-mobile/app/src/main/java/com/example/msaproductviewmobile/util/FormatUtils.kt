package com.example.msaproductviewmobile.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for common formatting and conversion operations.
 */
object FormatUtils {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val DATE_ONLY_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TIME_ONLY_FORMAT = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    /**
     * Format timestamp to human-readable date string.
     */
    fun formatTimestamp(timestamp: Long): String {
        return DATE_FORMAT.format(Date(timestamp))
    }

    /**
     * Format timestamp to date only.
     */
    fun formatDate(timestamp: Long): String {
        return DATE_ONLY_FORMAT.format(Date(timestamp))
    }

    /**
     * Format timestamp to time only.
     */
    fun formatTime(timestamp: Long): String {
        return TIME_ONLY_FORMAT.format(Date(timestamp))
    }

    /**
     * Format duration in milliseconds to human-readable string.
     */
    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return if (hours > 0) {
            "${hours}h ${minutes % 60}m"
        } else if (minutes > 0) {
            "${minutes}m ${seconds % 60}s"
        } else {
            "${seconds}s"
        }
    }
}
