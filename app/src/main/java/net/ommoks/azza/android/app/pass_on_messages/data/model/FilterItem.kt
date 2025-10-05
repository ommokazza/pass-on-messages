package net.ommoks.azza.android.app.pass_on_messages.data.model

import kotlinx.serialization.Serializable
import java.util.UUID
import java.io.Serializable as JavaSerializable

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

fun Filter.isMatched(title: String, text: String) : Boolean {

    val allRulesMatched = rules.isNotEmpty()
            && rules.stream().allMatch { rule ->
        when (rule.type) {
            RuleType.TitleContains -> title.contains(rule.phrase)

            RuleType.TitleIs -> title.trim() == rule.phrase.trim()
                    || title.replace(Regex("[\\u2068-\\u2069]"), "").trim() == rule.phrase.trim()

            RuleType.TextContains -> text.contains(rule.phrase)

            RuleType.TextNotContains -> !text.contains(rule.phrase)
        }
    }

    return allRulesMatched
}
