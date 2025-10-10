package net.ommoks.azza.android.app.pass_on_messages.data

import net.ommoks.azza.android.app.pass_on_messages.data.model.Filter

interface MainRepository {
    suspend fun saveFilters(filters: List<Filter>)
    suspend fun loadFilters() : List<Filter>

    suspend fun updateLastTimestamp(filter: Filter, timestampInMillisecond: Long)
    suspend fun getLastTimestamp(filter: Filter) : Long?
}
