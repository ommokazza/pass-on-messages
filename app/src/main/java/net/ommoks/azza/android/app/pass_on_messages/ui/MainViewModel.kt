package net.ommoks.azza.android.app.pass_on_messages.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.pass_on_messages.R
import net.ommoks.azza.android.app.pass_on_messages.data.AppPreferenceRepository
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.model.Filter
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val mainRepository: MainRepository,
    private val appPreferenceRepository: AppPreferenceRepository,
) : ViewModel() {

    sealed class FileIOResult {
        data class Success(val message: String) : FileIOResult()
        data class Failure(val message: String) : FileIOResult()
    }

    private val _fileIOResult = MutableSharedFlow<FileIOResult>()
    val fileIOResult = _fileIOResult.asSharedFlow()

    private val _filters = MutableStateFlow<List<Filter>>(mutableListOf())
    val filters: StateFlow<List<Filter>> = _filters.asStateFlow()
    private var firstLoad = true
    init {
        viewModelScope.launch {
            _filters.value = mainRepository.loadFilters()
        }
        monitorFilters()
    }

    val debugMode = appPreferenceRepository.getDebugModeFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    private fun monitorFilters() {
        viewModelScope.launch {
            filters.collect {
                if (firstLoad) {
                    firstLoad = false
                } else {
                    Log.d(TAG, "filters changed")
                    mainRepository.saveFilters(it)
                }
            }
        }
    }


    fun addFilter(filter: Filter, intoFirst: Boolean = false) {
        _filters.value = if (intoFirst) {
            listOf(filter) + _filters.value
        } else {
            _filters.value + filter
        }
    }

    fun addFilters(filters: List<Filter>) {
        _filters.value = _filters.value + filters
    }


    fun updateFilter(updatedFilter: Filter) {
        _filters.value = _filters.value.map { filter ->
            if (filter.id == updatedFilter.id) {
                updatedFilter
            } else {
                filter
            }
        }
    }

    fun deleteFilter(filterToDelete: Filter) {
        _filters.value = _filters.value - filterToDelete
    }

    fun replaceFilters(newFilters: List<Filter>) {
        _filters.value = newFilters
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

                val filters = Json.decodeFromString<List<Filter>>(jsonString)
                replaceFilters(filters)

                _fileIOResult.emit(FileIOResult.Success(
                    appContext.getString(R.string.toast_import_success)))
            } catch (e: Exception) {
                _fileIOResult.emit(FileIOResult.Failure(
                    appContext.getString(R.string.toast_import_fail) + ": ${e.message}"))
            }
        }
    }

    suspend fun setDebugMode(mode: Boolean) {
        withContext(Dispatchers.IO) {
            appPreferenceRepository.setDebugMode(mode)
        }
    }

    fun exportLog(uri: Uri) {
        viewModelScope.launch {
            //TODO: Implement this
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
