package net.ommoks.azza.android.app.pass_on_messages.data.model

import android.content.Context
import net.ommoks.azza.android.app.pass_on_messages.R

enum class RuleType {
    TitleContains,
    TitleIs,

    TextContains,
    TextNotContains,
}

fun RuleType.getStringRes(context: Context) : String {
    return when(this) {
        RuleType.TitleContains -> context.getString(R.string.rule_type_title_contains)
        RuleType.TitleIs -> context.getString(R.string.rule_type_title_is)
        RuleType.TextContains -> context.getString(R.string.rule_type_text_contains)
        RuleType.TextNotContains -> context.getString(R.string.rule_type_text_not_contains)
    }
}
