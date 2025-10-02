package net.ommoks.azza.android.app.passonnotifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.ommoks.azza.android.app.passonnotifications.databinding.FragmentMainBinding
import net.ommoks.azza.android.app.passonnotificationsimport.FilterAdapter
import java.util.UUID

@AndroidEntryPoint
class MainFragment : Fragment(), FilterAdapter.OnFilterActionsListener, EditFilterDialog.EditFilterDialogListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var history: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        history = binding.history
        recyclerView = binding.filter
        setupRecyclerView()
        applyViewModel()
    }

    private fun setupRecyclerView() {
        filterAdapter = FilterAdapter(this)
        recyclerView.adapter = filterAdapter

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filterAdapter
        }
    }

    private fun applyViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filters.collect { filters ->
                filterAdapter.submitList(mutableListOf<ListItem>(AddFilterItem) + filters)
            }
        }
    }

    override fun onFilterClick(filter: Filter) {
        val dialog = EditFilterDialog.newInstance(filter, this)
        dialog.show(childFragmentManager, "EditFilterDialog")
    }

    override fun onDeleteClick(filter: Filter) {
        viewModel.deleteFilter(filter)
    }

    override fun onAddFilterClick() {
        val newFilter = Filter(
            name = "",
            rules = mutableListOf(),
            passOnTo = "",
            id = UUID.randomUUID().toString()
        )

        val dialog = EditFilterDialog.newInstance(newFilter, this)
        dialog.show(childFragmentManager, "EditFilterDialog")
    }

    override fun onFilterSaved(filter: Filter) {
        val existingFilter = viewModel.filters.value.find { it.id == filter.id }
        if (existingFilter != null) {
            viewModel.updateFilter(filter)
        } else {
            viewModel.addFilter(filter, true)
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



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
