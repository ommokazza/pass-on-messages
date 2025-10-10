package net.ommoks.azza.android.app.pass_on_messages.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.pass_on_messages.common.Constants
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import net.ommoks.azza.android.app.pass_on_messages.data.model.Filter
import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterLog
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    @ApplicationContext val appContext: Context,
    val fileDataSource: FileDataSource
) : MainRepository {

    override suspend fun saveFilters(filters: List<Filter>) {
        if (filters.isEmpty()) {
            Log.e(TAG, "No filters to save.")
            return
        }

        val prevFilters = loadFilters()
        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(filters)
            fileDataSource.writeToInternalTextFile(Constants.FILTER_FILE, jsonString, false)

            val newFilterIds = filters.map { it -> it.id }
            prevFilters.forEach { oldFilter ->
                if (!newFilterIds.contains(oldFilter.id)) {
                    fileDataSource.deleteFile(LOG_FILE_PREFIX + oldFilter.id)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error saving filters", e)
        }
    }

    override suspend fun loadFilters(): List<Filter> {
        val jsonString = fileDataSource.readFromInternalTextFile(Constants.FILTER_FILE)

        val filters: MutableList<Filter> = if (jsonString.isNotEmpty()) {
            try {
                Json.decodeFromString<MutableList<Filter>>(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Error decoding filters, starting with empty list", e)
                mutableListOf()
            }
        } else {
            mutableListOf()
        }

        val listItems: MutableList<Filter> = filters.toMutableList()
        return listItems
    }

    override suspend fun updateLastTimestamp(filter: Filter, timestampInMillisecond: Long) {
        val filterLog = readFilterLogJson(filter) ?: FilterLog(mutableListOf())
        filterLog.timestamps.add(0, timestampInMillisecond)

        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(
                FilterLog(filterLog.timestamps.take(Constants.MAX_TIMESTAMP_COUNT).toMutableList()))
            fileDataSource.writeToInternalTextFile(filter.logFilename(), jsonString, false)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving filters", e)
        }
    }

    override suspend fun getLastTimestamp(filter: Filter): Long? {
        return readFilterLogJson(filter)?.timestamps?.max()
    }

    private suspend fun readFilterLogJson(filter: Filter) : FilterLog? {
        val jsonString = fileDataSource.readFromInternalTextFile(filter.logFilename())
        return if (jsonString.isNotEmpty()) {
            try {
                Json.decodeFromString<FilterLog>(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Error decoding filter log, starting with empty list", e)
                null
            }
        } else {
            null
        }
    }

    companion object {
        private const val TAG = "MainRepositoryImpl"
    }
}

private const val LOG_FILE_PREFIX = "log_"
fun Filter.logFilename() = LOG_FILE_PREFIX + this.id
