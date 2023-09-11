package com.condor.splashbox.data.user

import android.R.attr.fragment
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.condor.splashbox.App
import com.condor.splashbox.R
import com.condor.splashbox.adapters.PhotosListAdapter
import com.condor.splashbox.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private val photosAdapter by lazy { PhotosListAdapter() }
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var currentUserName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        val swipeRefresh: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshUser)
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
            val action = UserFragmentDirections.actionNavigationUserToPhotoDetailsFragment(photo)
            findNavController().navigate(action)
        }
        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun initList() {
        binding.photosListRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = photosAdapter
            photosAdapter.addLoadStateListener { loadState ->
                //Empty List
                binding.emptyListTextView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            photosAdapter.itemCount < 1
                if (loadState.refresh is LoadState.Error) {
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
            viewModel.currentUser.collectLatest { currentUser ->
                //currentUserName = currentUser?.name.toString()
                Log.d("UserFragment", "Current User = $currentUser")
                currentUserName = currentUser?.username.toString()
                binding.fullNameTextView.text = currentUser?.name.toString()
                binding.locationTextView.text = currentUser?.location.toString()

                Glide.with(requireContext())
                    .load(currentUser?.profile_image?.small)
                    .placeholder(R.drawable.ic_photo)
                    .circleCrop()
                    .into(binding.userImageView)

                getUserLikedPhotos(currentUser?.username.toString())
            }

        }
    }

    private fun getUserLikedPhotos(userName: String) {
        lifecycleScope.launch {
            viewModel.getUserLikedPhotos(userName).collectLatest { pagingData ->
                binding.emptyListTextView.isVisible = false
                photosAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
        }
    }


    private fun logout() {
        AlertDialog.Builder(
            requireContext(),
            com.google.android.material.R.style.Base_ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setTitle(getString(R.string.logout_dialog_title))
            .setMessage(getString(R.string.logout_dialog_message))
            .setPositiveButton(getString(R.string.logout_dialog_positive_button)) { _, _ ->
                // Delete the local data of the application.
                clearAppData()
                // Log out the user.
                finishAffinity(requireActivity())
            }
            .setNegativeButton(getString(R.string.logout_dialog_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun clearAppData() {
        try {
            val packageName = App.appContext.packageName
            val runtime = Runtime.getRuntime()
            runtime.exec("pm clear $packageName")
        } catch (e: Exception) {
            Log.d("UserFragment", "Clear App Data Error: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}