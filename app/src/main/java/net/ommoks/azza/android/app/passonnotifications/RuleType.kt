package net.ommoks.azza.android.app.passonnotifications

import android.content.Context

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
