package net.ommoks.azza.android.app.pass_on_messages.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Utils {

    @SuppressLint("SimpleDateFormat")
    fun dateTimeFromMillSec(epochMilli: Long?): String {
        if (epochMilli == null) return ""

        val currentDateTime =
            Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
    }
}
