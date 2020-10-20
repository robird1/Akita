package com.ulsee.ulti_a100.ui.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val url = args.url
        Log.d(TAG, "args.url: $url")

        viewModel = ViewModelProvider(this, AttendRecordFactory(AttendRecordRepository(url)))
            .get(AttendRecordViewModel::class.java)

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
//        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        initAdapter()
        binding.retryButton.setOnClickListener { adapter.retry() }

        (activity as MainActivity).setTitle("Records")

        observeRecordCount()
//        requestRecords()

        return binding.root
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ReposLoadStateAdapter { adapter.retry() },
            footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

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
        Log.d(TAG, "[Enter] requestRecords")
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
//            if (requestResult) {
                requestRecords()
//            }
        })
    }

}