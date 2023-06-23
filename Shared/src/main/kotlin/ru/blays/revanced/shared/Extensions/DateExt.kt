package ru.blays.revanced.shared.Extensions

import android.util.Log
import java.text.DateFormat
import java.time.Duration
import java.util.Calendar
import java.util.Date

private const val TAG = "DateExt"

val currentTime: Date get() = Calendar.getInstance().time

val getCurrentFormattedTime: (DateFormat) -> String
    get() = { formatter ->
        val time = currentTime
        formatter.format(time)
    }

val getDateObject: (String, DateFormat) -> Date?
    get() = { date, formatter ->
        formatter.parse(date)
    }

val Date.isInRange: (timeRange: Long) -> Boolean
    get() = { timeRange ->
        val currentTimeMills = currentTime.time
        val validatedTime = this.time
        val millsBetween = currentTimeMills - validatedTime
        val inHours = millsBetween.toHoursCount
        Log.d(TAG,
"""================================================
current time mills: $currentTimeMills;
validated time mills: $validatedTime;
mills between: $millsBetween;
inHours: $inHours
================================================""".trimIndent()
        )
        // Return
        inHours < timeRange
    }

val Long.toHoursCount: Long get() = Duration.ofMillis(this).toHours()
val Long.toMinutesCount: Long get() = Duration.ofMillis(this).toMinutes()