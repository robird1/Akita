package com.ulsee.ulti_a100.ui.record

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.databinding.FragmentAttendRecordsBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = AttendRecordFragment::class.java.simpleName

class AttendRecordFragment : Fragment() {
    private lateinit var binding: FragmentAttendRecordsBinding
    private val adapter = AttendRecordAdapter()
    private lateinit var viewModel: AttendRecordViewModel
    private val args: AttendRecordFragmentArgs by navArgs()
    private var searchJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendRecordsBinding.inflate(inflater, container, false)
        initViewModel()
        initSwipeRefreshView()
        initRecyclerView()
        initAdapter()
        binding.retryButton.setOnClickListener { adapter.retry() }
        setHasOptionsMenu(true)
        observeRecordCount()
        observeTemperatureUnit()
        observeClearRecords()

        (activity as MainActivity).setTitle("Records")

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, AttendRecordFactory(AttendRecordRepository(args.url)))
            .get(AttendRecordViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.attend_record_option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear_all -> {
                binding.progressBar.isVisible = true
                showClearDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ReposLoadStateAdapter { adapter.retry() },
            footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
//            Log.d(TAG, "[Enter] ${loadState.source.refresh} ")

            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            if (binding.swipeRefreshLayout.isRefreshing)
                binding.progressBar.isVisible = false

            if (loadState.source.refresh is LoadState.NotLoading)
                binding.swipeRefreshLayout.isRefreshing = false

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun requestRecords() {
//        Log.d(TAG, "[Enter] requestRecords")
        // Make sure we cancel the previous job before creating a new one
        val cancel = searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getRecords().collectLatest {
                Log.d(TAG, "[Enter] adapter.submitData(it)")
                adapter.submitData(it)
            }
        }
    }

    private fun observeRecordCount() {
        viewModel.recordCountResult.observe(viewLifecycleOwner, { requestResult ->
            Log.d(TAG, "[Enter] observeRecordCount result: $requestResult")
            if (requestResult) {
                if (viewModel.getRecordCount() > 0) {
//                    requestRecords()
                    viewModel.getTemperatureUnit()
                } else {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            } else {
                Toast.makeText(requireContext(), "Error(${viewModel.errorCode})", Toast.LENGTH_SHORT).show()
                viewModel.resetErrorCode()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    /**
     *  if unit request failed, observer will observe unit == "C"
     */
    private fun observeTemperatureUnit() {
        viewModel.temperatureUnit.observe(viewLifecycleOwner, { unit ->
//            Log.d(TAG, "[Enter] observeTemperatureUnit unit: $unit")
//            if (unit.isNotEmpty()) {
                adapter.setTemperatureUnit(unit)
                requestRecords()
//            }
        })
    }

    private fun initSwipeRefreshView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.setLastTemperatureUnit(adapter.getTemperatureUnit())
            viewModel.invalidatePagingSource()
            viewModel.loadRecordCount()
        }
    }

    private fun showClearDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Clear all records?")
            .setPositiveButton(getString(R.string.yes))
            { _, _ ->
                clearAllRecords()
            }
            .setNegativeButton(getString(R.string.no)
            ) { dialog, _ ->
                dialog.dismiss()
                binding.progressBar.isVisible = false
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun clearAllRecords() {
        viewModel.clearAllRecords()
    }

    private fun observeClearRecords() {
        viewModel.clearResult.observe(viewLifecycleOwner, { clearResult ->
            binding.progressBar.isVisible = false
            if (clearResult) {
                viewModel.invalidatePagingSource()
                Toast.makeText(requireContext(), "clear success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error(${viewModel.errorCode})", Toast.LENGTH_SHORT).show()
                viewModel.resetErrorCode()
            }
        })
    }

}