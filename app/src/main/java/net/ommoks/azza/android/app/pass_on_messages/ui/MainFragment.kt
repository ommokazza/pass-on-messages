package net.ommoks.azza.android.app.pass_on_messages.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.ommoks.azza.android.app.pass_on_messages.R
import net.ommoks.azza.android.app.pass_on_messages.databinding.FragmentMainBinding
import net.ommoks.azza.android.app.pass_on_messages.ui.MainViewModel.FileIOResult
import net.ommoks.azza.android.app.pass_on_messages.ui.model.AddFilterItem
import net.ommoks.azza.android.app.pass_on_messages.ui.model.FilterItem
import net.ommoks.azza.android.app.pass_on_messages.ui.model.ListItem
import java.util.UUID

@AndroidEntryPoint
class MainFragment : Fragment(), FilterAdapter.OnFilterActionsListener, EditFilterDialog.EditFilterDialogListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var recyclerView: RecyclerView

    private val exportFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportFilters(it) }
    }

    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importFilters(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.filter
        setupRecyclerView()
        setupMenu()
        applyViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.export_rules -> {
                        exportFileLauncher.launch("pass-on-messages-filters.json")
                        true
                    }
                    R.id.import_rules -> {
                        importFileLauncher.launch("application/json")
                        true
                    }
                    else -> false // 처리하지 않은 경우 false 반환
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
            viewModel.filters.collect { uiState ->
                filterAdapter.submitList(mutableListOf<ListItem>(AddFilterItem) + uiState)
            }
        }

        lifecycleScope.launch {
            viewModel.fileIOResult.collect { result ->
                val message = when (result) {
                    is FileIOResult.Success -> result.message
                    is FileIOResult.Failure -> result.message
                }
                Toast.makeText(
                    this@MainFragment.requireActivity(),
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onFilterClick(filter: FilterItem) {
        val dialog = EditFilterDialog.Companion.newInstance(filter, this)
        dialog.show(childFragmentManager, "EditFilterDialog")
    }

    override fun onDeleteClick(filter: FilterItem) {
        viewModel.deleteFilter(filter)
    }

    override fun onAddFilterClick() {
        val newFilter = FilterItem(
            name = "",
            rules = mutableListOf(),
            passOnTo = "",
            id = UUID.randomUUID().toString()
        )

        val dialog = EditFilterDialog.Companion.newInstance(newFilter, this)
        dialog.show(childFragmentManager, "EditFilterDialog")
    }

    override fun onFilterSaved(filter: FilterItem) {
        val existingFilter = viewModel.filters.value.find { it.id == filter.id }
        if (existingFilter != null) {
            viewModel.updateFilter(filter)
        } else {
            viewModel.addFilter(filter, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
