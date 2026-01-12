package com.astro.news.util

import android.content.Context
import com.astro.feature.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

/**
 * Formats an ISO 8601 date string to a relative time string using localized string resources.
 * 
 * @param context Android context to access string resources
 * @param isoDateString The ISO 8601 formatted date string (e.g., "2023-01-01T00:00:00Z")
 * @return A relative time string (e.g., "2 days ago", "1 hour ago")
 */
fun formatRelativeTime(context: Context, isoDateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        
        val date = sdf.parse(isoDateString) ?: return isoDateString
        val now = Date()
        
        val diffInMillis = now.time - date.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24
        val diffInWeeks = diffInDays / 7
        val diffInMonths = diffInDays / 30
        val diffInYears = diffInDays / 365
        
        when {
            diffInSeconds < 0 -> context.getString(R.string.just_now)
            diffInSeconds < 60 -> {
                val seconds = abs(diffInSeconds).toInt()
                context.resources.getQuantityString(R.plurals.seconds_ago, seconds, seconds)
            }
            diffInMinutes < 60 -> {
                val minutes = abs(diffInMinutes).toInt()
                context.resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
            }
            diffInHours < 24 -> {
                val hours = abs(diffInHours).toInt()
                context.resources.getQuantityString(R.plurals.hours_ago, hours, hours)
            }
            diffInDays < 7 -> {
                val days = abs(diffInDays).toInt()
                context.resources.getQuantityString(R.plurals.days_ago, days, days)
            }
            diffInWeeks < 4 -> {
                val weeks = abs(diffInWeeks).toInt()
                context.resources.getQuantityString(R.plurals.weeks_ago, weeks, weeks)
            }
            diffInMonths < 12 -> {
                val months = abs(diffInMonths).toInt()
                context.resources.getQuantityString(R.plurals.months_ago, months, months)
            }
            else -> {
                val years = abs(diffInYears).toInt()
                context.resources.getQuantityString(R.plurals.years_ago, years, years)
            }
        }
    } catch (e: Exception) {
        isoDateString
    }
}
