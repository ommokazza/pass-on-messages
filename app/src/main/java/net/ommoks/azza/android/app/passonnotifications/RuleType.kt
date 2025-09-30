package net.ommoks.azza.android.app.passonnotifications

enum class RuleType(val key: String, val text: String) {
    TitleContains("titleContains", "Title Contains"),
    TitleIs("titleIs", "Title Is"),

    TextContains("textContains", "Text Contains"),
    TextNotContains("textNotContains", "Text Not Contains");

    companion object {
        fun fromText(text: String): RuleType {
            return entries.find { it.text == text } ?: TextContains
        }
    }
}
