package net.ommoks.azza.android.app.passonnotifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _filters = MutableStateFlow<List<Filter>>(emptyList())
    val filters: StateFlow<List<Filter>> = _filters.asStateFlow()

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
}
