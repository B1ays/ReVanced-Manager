package ru.blays.revanced.shared.Extensions

import android.annotation.SuppressLint
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "DateExt"

private const val format = "yyyy-MM-dd HH:mm"

@SuppressLint("ConstantLocale")
val defaultFormatter = SimpleDateFormat(format, Locale.getDefault())

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