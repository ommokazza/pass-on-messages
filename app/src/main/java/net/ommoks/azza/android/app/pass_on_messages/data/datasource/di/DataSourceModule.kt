package net.ommoks.azza.android.app.pass_on_messages.data.datasource.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.impl.FileDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    fun provideFileDataSource(
        @ApplicationContext appContext: Context
    ) : FileDataSource = FileDataSourceImpl(appContext)
}
