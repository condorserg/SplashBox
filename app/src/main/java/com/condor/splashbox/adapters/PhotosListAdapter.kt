package com.condor.splashbox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.condor.splashbox.R
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.databinding.ItemPhotoBinding

class PhotosListAdapter : PagingDataAdapter<Photo, PhotosListAdapter.PhotosViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {

        return PhotosViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }

    private var onItemClickListener: ((Photo) -> Unit)? = null

    fun setOnItemClickListener(listener: (Photo) -> Unit) {
        onItemClickListener = listener
    }

    inner class PhotosViewHolder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            itemView.apply {
                with(binding) {
                    userNameTextView.text = photo.user?.name
                    likesTextView.text = photo.likes.toString()
                }
                //photo
                Glide.with(context)
                    .load(photo.urls.small)
                    .placeholder(R.drawable.ic_photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_photo)
                    .into(binding.photoImageView)
                //user's avatar
                Glide.with(context)
                    .load(photo.user?.profile_image?.small)
                    .placeholder(R.drawable.ic_photo)
                    .circleCrop()
                    .into(binding.userImageView)
                //likes
                if (photo.liked_by_user == false) {
                    Glide.with(itemView)
                        .load(R.drawable.ic_favorite_border_24)
                        .into(binding.likesImageView)
                } else {
                    Glide.with(itemView)
                        .load(R.drawable.ic_favorite)
                        .into(binding.likesImageView)
                }
                setOnClickListener {
                    onItemClickListener?.let {
                        it(photo)
                    }
                }
            }
        }
    }
}
