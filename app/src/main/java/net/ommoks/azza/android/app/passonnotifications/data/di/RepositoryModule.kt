package net.ommoks.azza.android.app.passonnotifications.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import net.ommoks.azza.android.app.passonnotifications.data.impl.MainRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        @ApplicationContext appContext: Context
    ) : MainRepository = MainRepositoryImpl(appContext)
}
