package net.ommoks.azza.android.app.pass_on_messages.data.model

import kotlinx.serialization.Serializable
import net.ommoks.azza.android.app.pass_on_messages.ui.model.FilterRule
import java.util.UUID

@Serializable
data class FilterModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rules: MutableList<FilterRule>,
    val passOnTo: String // Phone number
)
