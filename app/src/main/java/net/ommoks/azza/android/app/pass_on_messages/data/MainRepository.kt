package net.ommoks.azza.android.app.pass_on_messages.data

import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterModel

interface MainRepository {
    suspend fun saveFilters(fModels: List<FilterModel>)
    suspend fun loadFilters() : List<FilterModel>

    suspend fun updateLastTimestamp(fModel: FilterModel, timestampInMillisecond: Long)
    suspend fun getLastTimestamp(fModel: FilterModel) : Long?
}
