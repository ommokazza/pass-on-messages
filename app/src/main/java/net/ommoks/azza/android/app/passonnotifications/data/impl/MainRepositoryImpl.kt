package net.ommoks.azza.android.app.passonnotifications.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.passonnotifications.common.Constants
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import net.ommoks.azza.android.app.passonnotifications.data.datasource.FileDataSource
import net.ommoks.azza.android.app.passonnotifications.data.model.Filter
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

        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(filters)
            fileDataSource.writeToInternalTextFile(Constants.FILTER_FILE, jsonString, false)
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

    companion object {
        private const val TAG = "MainRepositoryImpl"
    }
}
