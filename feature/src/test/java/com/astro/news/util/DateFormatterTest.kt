package com.astro.news.util

import android.content.Context
import android.content.res.Resources
import com.astro.feature.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class DateFormatterTest {

    private lateinit var context: Context
    private lateinit var resources: Resources
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    @Before
    fun setup() {
        resources = mockk(relaxed = true)
        context = mockk(relaxed = true)
        every { context.resources } returns resources
        every { context.getString(R.string.just_now) } returns "Just now"
        
        setupPluralMocks()
    }

    private fun setupPluralMocks() {
        every { resources.getQuantityString(R.plurals.seconds_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 second ago" else "$count seconds ago"
        }
        
        every { resources.getQuantityString(R.plurals.minutes_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 minute ago" else "$count minutes ago"
        }
        
        every { resources.getQuantityString(R.plurals.hours_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 hour ago" else "$count hours ago"
        }
        
        every { resources.getQuantityString(R.plurals.days_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 day ago" else "$count days ago"
        }
        
        every { resources.getQuantityString(R.plurals.weeks_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 week ago" else "$count weeks ago"
        }
        
        every { resources.getQuantityString(R.plurals.months_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 month ago" else "$count months ago"
        }
        
        every { resources.getQuantityString(R.plurals.years_ago, any(), any()) } answers {
            val count = secondArg<Int>()
            if (count == 1) "1 year ago" else "$count years ago"
        }
    }

    private fun createDateString(secondsAgo: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.SECOND, -secondsAgo.toInt())
        return dateFormat.format(calendar.time)
    }


    @Test
    fun `Given date 1 second ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(1)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 second ago", result)
    }

    @Test
    fun `Given date 30 seconds ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(30)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("30 seconds ago", result)
    }

    @Test
    fun `Given date 1 minute ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 minute ago", result)
    }

    @Test
    fun `Given date 45 minutes ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(45 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("45 minutes ago", result)
    }

    @Test
    fun `Given date 1 hour ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 hour ago", result)
    }

    @Test
    fun `Given date 12 hours ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(12 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("12 hours ago", result)
    }

    @Test
    fun `Given date 1 day ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 day ago", result)
    }

    @Test
    fun `Given date 5 days ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(5 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("5 days ago", result)
    }

    @Test
    fun `Given date 1 week ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(7 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 week ago", result)
    }

    @Test
    fun `Given date 3 weeks ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(21 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("3 weeks ago", result)
    }

    @Test
    fun `Given date 1 month ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(30 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 month ago", result)
    }

    @Test
    fun `Given date 6 months ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(180 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("6 months ago", result)
    }

    @Test
    fun `Given date 1 year ago, When formatting relative time, Then returns singular form`() {
        val dateString = createDateString(365 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 year ago", result)
    }

    @Test
    fun `Given date 3 years ago, When formatting relative time, Then returns plural form`() {
        val dateString = createDateString(3 * 365 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("3 years ago", result)
    }

    @Test
    fun `Given invalid date format, When formatting relative time, Then returns original string`() {
        val invalidDate = "invalid-date-format"
        
        val result = formatRelativeTime(context, invalidDate)
        
        assertEquals(invalidDate, result)
    }

    @Test
    fun `Given empty string, When formatting relative time, Then returns empty string`() {
        val emptyDate = ""
        
        val result = formatRelativeTime(context, emptyDate)
        
        assertEquals(emptyDate, result)
    }

    @Test
    fun `Given date with null characters, When formatting relative time, Then returns original string`() {
        val malformedDate = "2024-01-01\u0000T00:00:00Z"
        
        val result = formatRelativeTime(context, malformedDate)
        
        assertEquals(malformedDate, result)
    }

    @Test
    fun `Given future date, When formatting relative time, Then returns just now`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.HOUR, 1)
        val futureDate = dateFormat.format(calendar.time)
        
        val result = formatRelativeTime(context, futureDate)
        
        assertEquals("Just now", result)
    }

    @Test
    fun `Given date 59 seconds ago, When formatting relative time, Then returns seconds format`() {
        val dateString = createDateString(59)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("59 seconds ago", result)
    }

    @Test
    fun `Given date 60 seconds ago, When formatting relative time, Then transitions to minutes format`() {
        val dateString = createDateString(60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 minute ago", result)
    }

    @Test
    fun `Given date 59 minutes ago, When formatting relative time, Then returns minutes format`() {
        val dateString = createDateString(59 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("59 minutes ago", result)
    }

    @Test
    fun `Given date 60 minutes ago, When formatting relative time, Then transitions to hours format`() {
        val dateString = createDateString(60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 hour ago", result)
    }

    @Test
    fun `Given date 23 hours ago, When formatting relative time, Then returns hours format`() {
        val dateString = createDateString(23 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("23 hours ago", result)
    }

    @Test
    fun `Given date 24 hours ago, When formatting relative time, Then transitions to days format`() {
        val dateString = createDateString(24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 day ago", result)
    }

    @Test
    fun `Given date 6 days ago, When formatting relative time, Then returns days format`() {
        val dateString = createDateString(6 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("6 days ago", result)
    }

    @Test
    fun `Given date 7 days ago, When formatting relative time, Then transitions to weeks format`() {
        val dateString = createDateString(7 * 24 * 60 * 60)
        
        val result = formatRelativeTime(context, dateString)
        
        assertEquals("1 week ago", result)
    }

    @Test
    fun `Given API date format, When formatting relative time, Then returns non-empty string`() {
        val apiDate = "2024-01-15T14:30:00Z"
        
        val result = formatRelativeTime(context, apiDate)
        
        assert(result.isNotEmpty())
    }
}
