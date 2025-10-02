package net.ommoks.azza.android.app.passonnotifications.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import net.ommoks.azza.android.app.passonnotifications.data.model.Filter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val appContext: Context,
    val mainRepository: MainRepository
) : ViewModel() {

    private val _filters = MutableStateFlow<List<Filter>>(mutableListOf())
    val filters: StateFlow<List<Filter>> = _filters.asStateFlow()
    private var firstLoad = true
    init {
        viewModelScope.launch {
            _filters.value = mainRepository.loadFilters()
        }
        monitorFilters()
    }

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

    companion object {
        private const val TAG = "MainViewModel"
    }
}
