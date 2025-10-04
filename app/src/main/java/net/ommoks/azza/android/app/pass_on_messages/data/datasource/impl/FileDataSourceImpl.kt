package net.ommoks.azza.android.app.pass_on_messages.data.datasource.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import net.ommoks.azza.android.app.pass_on_messages.data.datasource.FileDataSource
import java.io.IOException
import javax.inject.Inject

class FileDataSourceImpl @Inject constructor(
    @ApplicationContext val appContext: Context
): FileDataSource {
    override suspend fun writeToInternalTextFile(filename: String, content: String, append: Boolean) {
        val mode = if (append) Context.MODE_APPEND else Context.MODE_PRIVATE
        try {
            appContext.openFileOutput(filename, mode).use { stream ->
                stream.write((content + "\n").toByteArray())
            }
        } catch (e: IOException) {
            Log.e("FileWriteError", "Failed to write to file $filename", e)
        }
    }

    override suspend fun readFromInternalTextFile(filename: String): String {
        return try {
            appContext.openFileInput(filename).use { stream ->
                stream.bufferedReader().use {
                    it.readText()
                }
            }
        } catch (e: IOException) {
            Log.e("FileReadError", "Failed to read file $filename", e)
            ""
        }
    }
}
