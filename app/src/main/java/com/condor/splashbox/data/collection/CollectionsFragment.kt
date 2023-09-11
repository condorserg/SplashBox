package com.condor.splashbox.data.collection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.condor.splashbox.R
import com.condor.splashbox.adapters.CollectionsAdapter
import com.condor.splashbox.databinding.FragmentCollectionsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionsFragment : Fragment(R.layout.fragment_collections) {

    private val viewModel: CollectionsViewModel by viewModels()
    private val collectionsAdapter by lazy { CollectionsAdapter() }
    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        val view = binding.root
        val swipeRefresh: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshCollections)
        swipeRefresh.setColorSchemeResources(R.color.amber_500)
        swipeRefresh.setOnRefreshListener {
            initList()
            bindViewModel()
            swipeRefresh.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initList()
        bindViewModel()

        collectionsAdapter.setOnItemClickListener { collection ->
            val action =
                CollectionsFragmentDirections.actionCollectionsFragmentToCollectionPhotosFragment(
                    collection
                )
            findNavController().navigate(action)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initList() {
        binding.collectionsListRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = collectionsAdapter
            //progress bar
            collectionsAdapter.addLoadStateListener { loadState ->
                binding.progressBarCollections.isVisible = loadState.refresh is LoadState.Loading
                //Empty List
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    collectionsAdapter.itemCount < 1
                ) {
                    binding.collectionsListRecyclerView.isVisible = false
                    binding.emptyListTextView.isVisible = true
                } else {
                    binding.emptyListTextView.isVisible = false
                }
                if (loadState.refresh is LoadState.Error) {
                    binding.collectionsListRecyclerView.isVisible = false
                    binding.emptyListTextView.text = context.getString(R.string.errorLoading)
                    binding.emptyListTextView.isVisible = true
                } else {
                    binding.emptyListTextView.isVisible = false
                }
            }
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            viewModel.getCollections().collectLatest { pagingData ->
                binding.emptyListTextView.isVisible = false
                collectionsAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}