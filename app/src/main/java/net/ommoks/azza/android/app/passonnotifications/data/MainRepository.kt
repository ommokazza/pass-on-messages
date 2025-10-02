package net.ommoks.azza.android.app.passonnotifications.data

import net.ommoks.azza.android.app.passonnotifications.Filter

interface MainRepository {
    suspend fun saveFilters(filters: List<Filter>)
    suspend fun loadFilters() : List<Filter>
}
