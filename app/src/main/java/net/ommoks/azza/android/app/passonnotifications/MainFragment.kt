package net.ommoks.azza.android.app.passonnotifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.passonnotificationsimport.FilterAdapter

class MainFragment : Fragment(), FilterAdapter.OnFilterActionsListener, EditFilterDialog.EditFilterDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var history: TextView
    private var currentEditPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        history = view.findViewById(R.id.history)
        recyclerView = view.findViewById(R.id.filter)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val filterList = Utils.loadFilters(requireContext().applicationContext)
        filterList.add(AddFilterItem)

        filterAdapter = FilterAdapter(filterList, this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filterAdapter
        }
    }

    override fun onModifyFilterClick(filter: Filter, position: Int) {
        currentEditPosition = position
        val dialog = EditFilterDialog.newInstance(filter, this)
        dialog.show(childFragmentManager, "EditFilterDialog")
    }

    override fun onFilterSaved(filter: Filter) {
        if (currentEditPosition != -1) {
            filterAdapter.updateFilter(filter, currentEditPosition)
            currentEditPosition = -1
        }
    }

    override fun onResume() {
        super.onResume()
        updateHistory()
    }

    private fun updateHistory() {
        val historyRaw = Utils.readFromInternalFile(requireContext(), Constants.LOG_FILE)
        val historyReversed = historyRaw.split("\n").reversed().filter { it.isNotEmpty() }
        history.text = historyReversed.joinToString("\n")
    }

    override fun onPause() {
        super.onPause()
        saveFilters()
    }

    private fun saveFilters() {
        val filtersToSave = filterAdapter.getFilterItems()

        if (filtersToSave.isEmpty()) {
            Log.d("FilterSave", "No filters to save.")
            return
        }

        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(filtersToSave)
            Utils.writeToInternalFile(requireContext(), Constants.FILTER_FILE, jsonString)
            Log.d("FilterSave", "Filters saved successfully")
        } catch (e: Exception) {
            Log.e("FilterSave", "Error saving filters", e)
        }
    }
}
