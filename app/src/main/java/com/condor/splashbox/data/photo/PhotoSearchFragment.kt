package com.condor.splashbox.data.photo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.condor.splashbox.R
import com.condor.splashbox.adapters.PhotosListAdapter
import com.condor.splashbox.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotoSearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: PhotosListViewModel by viewModels()
    private val photosSearchAdapter by lazy { PhotosListAdapter() }
    private var currentJob: Job? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        bindViewModel()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        photosSearchAdapter.setOnItemClickListener { photo ->
            val action =
                PhotoSearchFragmentDirections.actionPhotoSearchFragmentToPhotoDetailsFragment(photo)
            findNavController().navigate(action)
        }

        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchPhotos(newText)
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.searchView2.apply {
            isFocusable = true
            isIconified = false
            post {
                binding.searchView2.requestFocus()
            }
        }
    }

    fun searchPhotos(query: String) {
        currentJob?.cancel()
        currentJob = lifecycleScope.launch {
            if (query.length >= 3)
                viewModel.searchPhotos(query).collectLatest { pagingData ->
                    photosSearchAdapter.submitData(pagingData)
                }
        }
    }

    private fun initList() {
        binding.photosSearchRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = photosSearchAdapter
            photosSearchAdapter.addLoadStateListener { loadState ->
                //progress bar
                binding.searchProgressBar.isVisible = loadState.refresh is LoadState.Loading
                //Empty List
                binding.noSearchResultsTextView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            photosSearchAdapter.itemCount < 1
                if (loadState.source.refresh is LoadState.Error) {
                    showErrorToast(getString(R.string.errorLoading))
                }
            }
        }
    }

    private fun showErrorToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun bindViewModel() {
        viewModel.showToast
            .observe(viewLifecycleOwner, ::showErrorToast)
    }

    override fun onDestroy() {
        currentJob?.cancel()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}