package net.ommoks.azza.android.app.pass_on_messages.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.pass_on_messages.R
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.model.FilterModel
import net.ommoks.azza.android.app.pass_on_messages.ui.model.FilterItem
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

fun FilterItem.toModel() =
    FilterModel(this.id, this.name, this.rules, this.passOnTo)

fun FilterModel.toItem(timestamp: Long?) =
    FilterItem(this.id, this.name, this.rules, this.passOnTo, timestamp)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val appContext: Context,
    val mainRepository: MainRepository
) : ViewModel() {

    sealed class FileIOResult {
        data class Success(val message: String) : FileIOResult()
        data class Failure(val message: String) : FileIOResult()
    }

    private val _fileIOResult = MutableSharedFlow<FileIOResult>()
    val fileIOResult = _fileIOResult.asSharedFlow()

    private val _filters = MutableStateFlow<List<FilterItem>>(mutableListOf())
    val filters: StateFlow<List<FilterItem>> = _filters.asStateFlow()
    init {
        viewModelScope.launch {
            _filters.value = mainRepository.loadFilters().map { model ->
                model.toItem(mainRepository.getLastTimestamp(model))
            }
        }
    }

    fun addFilter(filter: FilterItem, intoFirst: Boolean = false) {
        val newFilter = listOf(filter)
        _filters.value = if (intoFirst) {
            newFilter + _filters.value
        } else {
            _filters.value + newFilter
        }
        saveFilters()
    }

    fun updateFilter(updatedFilter: FilterItem) {
        _filters.value = _filters.value.map { filter ->
            if (filter.id == updatedFilter.id) {
                updatedFilter
            } else {
                filter
            }
        }
        saveFilters()
    }

    fun deleteFilter(filterToDelete: FilterItem) {
        _filters.value = _filters.value - filterToDelete
        saveFilters()
    }

    fun replaceFilters(newFilters: List<FilterItem>) {
        _filters.value = newFilters
        saveFilters()
    }

    private fun saveFilters() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.saveFilters(_filters.value.map { it -> it.toModel() })
        }
    }

    fun exportFilters(uri: Uri) {
        viewModelScope.launch {
            try {
                val jsonString = Json.encodeToString( _filters.value)

                appContext.contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                    FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                        it.write(jsonString.toByteArray())
                    }
                }
                _fileIOResult.emit(FileIOResult.Success(
                    appContext.getString(R.string.toast_export_success)))
            } catch (e: Exception) {
                _fileIOResult.emit(FileIOResult.Failure(
                    appContext.getString(R.string.toast_export_fail) + ": ${e.message}"))
            }
        }
    }

    fun importFilters(uri: Uri) {
        viewModelScope.launch {
            try {
                val stringBuilder = StringBuilder()
                appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }
                val jsonString = stringBuilder.toString()

                val filters = Json.decodeFromString<List<FilterItem>>(jsonString)
                replaceFilters(filters)

                _fileIOResult.emit(FileIOResult.Success(
                    appContext.getString(R.string.toast_import_success)))
            } catch (e: Exception) {
                _fileIOResult.emit(FileIOResult.Failure(
                    appContext.getString(R.string.toast_import_fail) + ": ${e.message}"))
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
