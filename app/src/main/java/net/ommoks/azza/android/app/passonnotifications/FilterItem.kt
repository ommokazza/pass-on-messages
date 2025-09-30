package net.ommoks.azza.android.app.passonnotifications

import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable
import java.util.UUID

// Base interface for all items in the Filter RecyclerView list
sealed interface ListItem

// Represents a single filtering rule
@Serializable
data class FilterRule(val id: String = UUID.randomUUID().toString(),
                      var type: RuleType,
                      var phrase: String
) : ListItem, JavaSerializable // Rules are a part of a Filter

// Represents a complete filter configuration
@Serializable
data class Filter(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val rules: MutableList<FilterRule>,
    var passOnTo: String // Phone number
) : ListItem, JavaSerializable

// A special object to represent the 'Add Filter' button in the list
object AddFilterItem : ListItem