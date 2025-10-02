package net.ommoks.azza.android.app.passonnotifications.data.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
        //TODO: Implement
        return emptyList()
    }
}
