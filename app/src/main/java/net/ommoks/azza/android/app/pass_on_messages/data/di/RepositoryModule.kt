package net.ommoks.azza.android.app.pass_on_messages.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.ommoks.azza.android.app.pass_on_messages.data.AppPreferenceRepository
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import net.ommoks.azza.android.app.pass_on_messages.data.impl.AppPreferenceRepositoryImpl
import net.ommoks.azza.android.app.pass_on_messages.data.impl.MainRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        @ApplicationContext appContext: Context,
        fileDataSource: FileDataSource
    ) : MainRepository = MainRepositoryImpl(appContext, fileDataSource)

    @Singleton
    @Provides
    fun provideAppPreferenceRepository(
        @ApplicationContext appContext: Context
    ) : AppPreferenceRepository = AppPreferenceRepositoryImpl(appContext)
}
