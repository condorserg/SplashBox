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
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.condor.splashbox.R
import com.condor.splashbox.adapters.PhotosListAdapter
import com.condor.splashbox.databinding.FragmentCollectionPhotosBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionPhotosFragment : Fragment(R.layout.fragment_collection_photos) {

    private val args: CollectionPhotosFragmentArgs by navArgs()
    private val viewModel: CollectionsViewModel by viewModels()
    private val photosAdapter by lazy { PhotosListAdapter() }
    private var _binding: FragmentCollectionPhotosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionPhotosBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.collectionTitle) + args.collection.title

        initList()
        bindViewModel()

        photosAdapter.setOnItemClickListener { photo ->
            val action =
                CollectionPhotosFragmentDirections.actionCollectionPhotosFragmentToPhotoDetailsFragment(
                    photo
                )
            findNavController().navigate(action)
        }

    }

    private fun initList() {
        binding.photosListRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = photosAdapter

            photosAdapter.addLoadStateListener { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            }
        }
    }

    private fun bindViewModel() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getCollectionPhotos(collectionId = args.collection.id)
                .collectLatest { pagingData ->
                    photosAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}