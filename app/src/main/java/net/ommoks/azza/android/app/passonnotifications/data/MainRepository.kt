package net.ommoks.azza.android.app.passonnotifications.data

import net.ommoks.azza.android.app.passonnotifications.data.model.Filter

interface MainRepository {
    suspend fun saveFilters(filters: List<Filter>)
    suspend fun loadFilters() : List<Filter>
}
