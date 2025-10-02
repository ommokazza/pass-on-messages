package net.ommoks.azza.android.app.passonnotifications.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.passonnotifications.Constants
import net.ommoks.azza.android.app.passonnotifications.Filter
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import net.ommoks.azza.android.app.passonnotifications.data.datasource.FileDataSource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    @ApplicationContext val appContext: Context,
    val fileDataSource: FileDataSource
) : MainRepository {

    override suspend fun saveFilters(filters: List<Filter>) {
        //TODO: Implement
    }

    override suspend fun loadFilters(): List<Filter> {
        val jsonString = fileDataSource.readFromInternalTextFile(Constants.FILTER_FILE)

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

        val listItems: MutableList<Filter> = filters.toMutableList()
        return listItems
    }
}
