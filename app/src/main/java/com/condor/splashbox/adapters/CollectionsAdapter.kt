package com.condor.splashbox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.condor.splashbox.R
import com.condor.splashbox.data.collection.Collection
import com.condor.splashbox.databinding.ItemCollectionBinding

class CollectionsAdapter :
    PagingDataAdapter<Collection, CollectionsAdapter.CollectionsViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }

    private var onItemClickListener: ((Collection) -> Unit)? = null

    fun setOnItemClickListener(listener: (Collection) -> Unit) {
        onItemClickListener = listener
    }

    inner class CollectionsViewHolder(
        val binding: ItemCollectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collection: Collection) {
            itemView.apply {
                with(binding) {
                    userNameTextView.text = collection.user?.name
                    collectionTitleTextView.text = collection.title
                    collectionCountTextView.text = collection.total_photos.toString()
                }
                //collection image
                Glide.with(context)
                    .load(collection.cover_photo?.urls?.small)
                    .placeholder(R.drawable.ic_photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_photo)
                    .into(binding.collectionImageView)
                //user's avatar
                Glide.with(context)
                    .load(collection.user?.profile_image?.small)
                    .placeholder(R.drawable.ic_photo)
                    .circleCrop()
                    .into(binding.userImageView)
                setOnClickListener {
                    onItemClickListener?.let {
                        it(collection)
                    }
                }
            }
        }
    }
}
