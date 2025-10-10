package net.ommoks.azza.android.app.pass_on_messages.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FilterLog(
    val timestamps: MutableList<Long>
)
