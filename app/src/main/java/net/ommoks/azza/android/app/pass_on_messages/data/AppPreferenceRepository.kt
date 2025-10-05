package net.ommoks.azza.android.app.pass_on_messages.data

import kotlinx.coroutines.flow.Flow

interface AppPreferenceRepository {

    fun getDebugModeFlow() : Flow<Boolean>
    suspend fun setDebugMode(mode: Boolean)
}
