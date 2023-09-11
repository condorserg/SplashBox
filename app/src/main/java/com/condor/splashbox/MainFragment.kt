package com.condor.splashbox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.condor.splashbox.adapters.PhotosListAdapter
import com.condor.splashbox.data.photo.PhotosListViewModel
import com.condor.splashbox.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: PhotosListViewModel by viewModels()
    private val photosAdapter by lazy { PhotosListAdapter()}
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // here you can do logic when backPress is clicked
                activity?.finish()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        val swipeRefresh: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshMain)
        swipeRefresh.setColorSchemeResources(R.color.amber_500)
        swipeRefresh.setOnRefreshListener {
            initList()
            bindViewModel()
            swipeRefresh.isRefreshing = false
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()
        bindViewModel()

        photosAdapter.setOnItemClickListener { photo ->
            val action = MainFragmentDirections.actionMainFragmentToPhotoDetailsFragment(photo)
            findNavController().navigate(action)
        }

        binding.searchButton.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToPhotoSearchFragment())
        }

    }

    private fun initList() {
        Log.d("RemoteMediator", "Init List")
        binding.photosListRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = photosAdapter
            photosAdapter.addLoadStateListener { loadState ->
                //progress bar
                    binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    photosAdapter.itemCount < 1
                ) {
                    binding.photosListRecyclerView.isVisible = false
                    binding.emptyListTextView.isVisible = true
                } else {
                    binding.emptyListTextView.isVisible = false
                }
            }

        }
    }

    private fun showErrorToast(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun bindViewModel() {
        viewModel.showToast
            .observe(viewLifecycleOwner, ::showErrorToast)
        Log.d("MainFragment", "BindViewModel")
        lifecycleScope.launch {
            viewModel.photos.collectLatest { photos ->
                photosAdapter.submitData(photos)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
