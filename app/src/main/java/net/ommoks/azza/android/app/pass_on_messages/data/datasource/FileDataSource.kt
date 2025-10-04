package net.ommoks.azza.android.app.pass_on_messages.data.datasource

interface FileDataSource {

    suspend fun writeToInternalTextFile(filename: String, content: String, append: Boolean = false)
    suspend fun readFromInternalTextFile(filename: String): String
}
