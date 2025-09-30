package net.ommoks.azza.android.app.passonnotifications

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object Utils {

    fun keepOnlyNumbers(input: String): String {
        return input.filter { it.isDigit() }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateTimeFromMillSec(epochMilli: Long): String {
        val currentDateTime =
            Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return currentDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
    }

    /**
     * Writes content to a file in the app's internal storage.
     * This storage is private to the application.
     *
     * @param context The Context of the calling component.
     * @param fileName The name of the file to write to (e.g., "log.txt").
     * @param content The String content to write.
     * @param append If true, appends content to the end of the file; otherwise, overwrites it.
     */
    fun writeToInternalFile(context: Context,
                 fileName: String,
                 content: String,
                 append: Boolean = false) {
        val mode = if (append) Context.MODE_APPEND else Context.MODE_PRIVATE
        try {
            // openFileOutput returns a FileOutputStream
            context.openFileOutput(fileName, mode).use { stream ->
                // .use will automatically close the stream
                stream.write((content + "\n").toByteArray())
            }
        } catch (e: IOException) {
            Log.e("FileWriteError", "Failed to write to file $fileName", e)
        }
    }

    /**
     * Reads all content from a file in the app's internal storage.
     *
     * @param context The Context of the calling component.
     * @param fileName The name of the file to read from.
     * @return The content of the file as a String, or an empty string if the file doesn't exist or an error occurs.
     */
    fun readFromInternalFile(context: Context, fileName: String): String {
        return try {
            context.openFileInput(fileName).use { stream ->
                // Read the bytes and convert them to a String
                stream.bufferedReader().use {
                    it.readText()
                }
            }
        } catch (e: IOException) {
            Log.e("FileReadError", "Failed to read file $fileName", e)
            "" // Return empty string on error
        }
    }

    /**
     * Extracts the last N lines from a given string.
     *
     * @param text The input string, potentially with multiple lines.
     * @param linesCount The number of lines to retrieve from the end.
     * @return A single string containing the last N lines, joined by newline characters.
     */
    fun getLastLinesFromString(text: String, linesCount: Int): String {
        return text.lines()
            .takeLast(linesCount)
            .joinToString("\n")
    }

    fun loadFilters(appContext: Context): MutableList<ListItem> {
        val jsonString = Utils.readFromInternalFile(appContext, Constants.FILTER_FILE)

        val filters: MutableList<Filter> = if (jsonString.isNotEmpty()) {
            try {
                Json.decodeFromString<MutableList<Filter>>(jsonString)
            } catch (e: Exception) {
                Log.e("FilterLoad", "Error decoding filters, starting with empty list", e)
                mutableListOf()
            }
        } else {
            mutableListOf()
        }

        val listItems: MutableList<ListItem> = filters.toMutableList()
        return listItems
    }
}
