package com.condor.splashbox.data.photo

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.condor.splashbox.R
import com.condor.splashbox.adapters.OnItemClickListener
import com.condor.splashbox.adapters.PhotoDetailsAdapter
import com.condor.splashbox.databinding.FragmentPhotoDetailsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details),
    OnItemClickListener {

    private val args: PhotoDetailsFragmentArgs by navArgs()
    private val viewModel: PhotoDetailsViewModel by viewModels()
    private var photoAdapter: PhotoDetailsAdapter? = null

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPhotoDetails(args.photo.id)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        bindViewModel()

        binding.shareButton.setOnClickListener {
            val photoId = args.photo.id
            sharePhoto(photoId)
        }

        binding.downloadButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val isWritePermissionGranted = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                if (isWritePermissionGranted) {
                    downloadPhoto(args.photo.urls.raw.toString(), args.photo.id)
                } else {
                    requestWritePermission()
                }
            } else {
                downloadPhoto(args.photo.urls.raw.toString(), args.photo.id)
            }
        }
        if (args.photo.liked_by_user == true) {
            binding.likeButtonToolbar.setImageResource(R.drawable.ic_favorite)
        }
        binding.likeButtonToolbar.setOnClickListener {
            onLikeButtonClick(args.photo)
        }
    }

    private fun initList() {
        photoAdapter = PhotoDetailsAdapter(this)

        with(binding.photoDetailsRecyclerView) {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, ::updateLoadingState)
        viewModel.photo.observe(viewLifecycleOwner) {
            photoAdapter?.items = listOf(it)
        }
        viewModel.showToast
            .observe(viewLifecycleOwner, ::showErrorToast)
    }

    private fun showErrorToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.photoDetailsRecyclerView.isVisible = isLoading.not()
        binding.progressBar.isVisible = isLoading
    }

    private fun sharePhoto(id: String) {
        val url = "https://unsplash.com/photos/$id"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareLinkTitle)))
    }

    override fun onLocationButtonClick(item: Photo) {
        val locationString = with(item) {
            when {
                location?.city != null && location.country != null ->
                    "${location.city}, ${location.country}"

                location?.city != null && location.country == null -> location.city
                location?.city == null && location?.country != null -> location.country
                else -> null
            }
        }
        Log.d("LocationButton", "Location = ${item.location?.city}, ${item.location?.country}")
        Log.d("LocationButton", "Photo: $item")
        if (locationString != null) openLocationInMaps(locationString) else Toast.makeText(
            requireContext(),
            getString(R.string.locationNA),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onLikeButtonClick(item: Photo) {
        Log.d("LikeButton", "Like Button Clicked")
        var photoLiked = args.photo.liked_by_user
        var likesCount = args.photo.likes

        val holder = binding.photoDetailsRecyclerView.findViewHolderForAdapterPosition(0)
        val likeButton = holder?.itemView?.findViewById<ImageView>(R.id.likesImageView)
        val likesCountTextView = holder?.itemView?.findViewById<TextView>(R.id.likesTextView)

        if (photoLiked == false) {
            viewModel.likePhoto(item.id)
            photoLiked = true
            likesCount = likesCount?.plus(1)
            args.photo.likes = likesCount
            likeButton?.setImageResource(R.drawable.ic_favorite)
            binding.likeButtonToolbar.setImageResource(R.drawable.ic_favorite)
            args.photo.liked_by_user = photoLiked
            likesCountTextView?.text = likesCount.toString()
        } else {
            viewModel.unlikePhoto(item.id)
            likeButton?.setImageResource(R.drawable.ic_favorite_border_24)
            binding.likeButtonToolbar.setImageResource(R.drawable.ic_favorite_border_24)
            photoLiked = false
            likesCount = likesCount?.minus(1)
            args.photo.liked_by_user = photoLiked
            args.photo.likes = likesCount
            likesCountTextView?.text = likesCount.toString()
        }
    }


    private fun requestWritePermission() {
        requestPermissions(
            arrayOf(WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
        } else {
            Snackbar.make(
                requireView(),
                getString(R.string.requestPermissionToast),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun downloadPhoto(url: String, fileName: String) {
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.downloadPhoto(url, fileName)
            }

        } else {
            viewModel.downloadPhotoDM(url, fileName)
        }
    }

    private fun openLocationInMaps(location: String?) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
        val intent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(
                requireView(),
                getString(R.string.errorOpenLocation),
                Snackbar.LENGTH_SHORT
            ).show()
            Log.d("OpenLocation", "Open Location Error", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        photoAdapter = null
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }
}
