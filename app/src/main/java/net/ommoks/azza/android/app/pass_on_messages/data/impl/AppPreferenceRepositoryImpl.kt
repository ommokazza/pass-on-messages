package net.ommoks.azza.android.app.pass_on_messages.data.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.ommoks.azza.android.app.pass_on_messages.data.AppPreferenceRepository
import javax.inject.Inject

class AppPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : AppPreferenceRepository {

    private val debugModeKey = booleanPreferencesKey("debug_mode")

    override fun getDebugModeFlow(): Flow<Boolean> {
        return appContext.dataStore.data.map { prefs ->
            prefs[debugModeKey] ?: false }

    }

    override suspend fun setDebugMode(mode: Boolean) {
        appContext.dataStore.edit { prefs ->
            prefs[debugModeKey] = mode
        }
    }
}

private const val APP_PREFERENCE_NAME = "app_preferences"

private val Context.dataStore by preferencesDataStore(
    name = APP_PREFERENCE_NAME
)
